package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Card;
import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import cat.udl.eps.switchconfiguration.domain.Port;
import cat.udl.eps.switchconfiguration.repository.ConnectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@Controller
public class SaveConfigurationController {
    private final Logger logger = LoggerFactory.getLogger(GetConfigurationController.class);

    @Autowired
    private ConnectorRepository connectorRepository;

    @RequestMapping(value = "/connectors/{id}/saveConfig", method = RequestMethod.GET)
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/

    public @ResponseBody
    HttpStatus saveConfigAndOrCertify(@PathVariable("id") Long id, @RequestParam("directory") String directory, @RequestParam("certify") boolean certify) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        logger.info("User Requested To Save Configuration: " + String.valueOf(id));

        Connector connector = connectorRepository.findOne(id);
        Port port = connector.getConnectedTo();
        Card card = port.getIsInCard();
        Equipment equipment = card.getIsInEquipment();


        Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put("equipmentIP", equipment.getIP());
        urlParameters.put("username", equipment.getUsername());
        urlParameters.put("password", equipment.getPassword());

        //LOGIN AND GET COOKIES
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://{equipmentIP}/auth/?&username={username}&password={password}", String.class, urlParameters);
        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];

        if (forEntity.getStatusCode() == HttpStatus.OK) {
            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "copy certified " + directory + " make-running-directory");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookies);
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X capability"
            ResponseEntity<String> response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand}", HttpMethod.GET, entity, String.class, urlParameters);

            if (response.getStatusCode() != HttpStatus.OK){
                return response.getStatusCode();
            }
            try {
                Thread.sleep(60000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            urlParameters.put("cmdCommand", "write memory");
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand}", HttpMethod.GET, entity, String.class, urlParameters);

            if (response.getStatusCode() != HttpStatus.OK){
                return response.getStatusCode();
            }

            if(certify){
                urlParameters.put("cmdCommand", "copy running certified");
                response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand}", HttpMethod.GET, entity, String.class, urlParameters);
            }

            return response.getStatusCode();

        }
        return forEntity.getStatusCode();//NEED CHECK
    }
}
