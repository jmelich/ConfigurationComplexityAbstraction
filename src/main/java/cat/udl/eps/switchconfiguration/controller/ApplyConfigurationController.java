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

import javax.xml.ws.Response;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ApplyConfigurationController {
    private final Logger logger = LoggerFactory.getLogger(GetConfigurationController.class);


    @Autowired
    private ConnectorRepository connectorRepository;

    @RequestMapping(value = "/connectors/{id}/setVLAN", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody
    ResponseEntity setVLAN(@PathVariable("id") Long id, @RequestParam("vlan") String vlan)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        logger.info("User Requested To Add Connector to VLAN: " + String.valueOf(id));

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

        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://{equipmentIP}/auth/?&username={username}&password={password}",
                String.class,
                urlParameters
        );

        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];

        if (forEntity.getStatusCode() == HttpStatus.OK) {
            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "vlan " + vlan + " members port");
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
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} untagged",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            return responseGenerator(response);
        }
        return responseGenerator(forEntity);
    }

    @RequestMapping(value = "/connectors/{id}/setAdministrativeStatus", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody
    ResponseEntity<String> setAdministrativeStatus(@PathVariable("id") Long id, @RequestParam("status") String status)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        logger.info("User Requested To Change Connector status to: " + status);

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

        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://{equipmentIP}/auth/?&username={username}&password={password}",
                String.class,
                urlParameters
        );

        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];

        if (forEntity.getStatusCode() == HttpStatus.OK) {
            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "interfaces ");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));
            urlParameters.put("status", status);

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookies);
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X capability"
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} admin-state {status}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );
            return responseGenerator(response);
        }
        return responseGenerator(forEntity);
    }

    @RequestMapping(value = "/connectors/{id}/setDuplexMode", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody
    ResponseEntity setDuplexMode(@PathVariable("id") Long id, @RequestParam("mode") String mode)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        logger.info("User Requested To Change Duplex mode to: " + mode);

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

        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://{equipmentIP}/auth/?&username={username}&password={password}",
                String.class,
                urlParameters
        );

        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];

        if (forEntity.getStatusCode() == HttpStatus.OK) {
            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "interfaces ");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));
            urlParameters.put("mode", mode);

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookies);
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X capability"
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} duplex {mode}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            return responseGenerator(response);
        }
        return responseGenerator(forEntity);
    }

    @RequestMapping(value = "/connectors/{id}/setPortSpeed", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody
    ResponseEntity setPortSpeed(@PathVariable("id") Long id, @RequestParam("speed") String speed)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        logger.info("User Requested To Change Speed to: " + speed);

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

        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://{equipmentIP}/auth/?&username={username}&password={password}",
                String.class,
                urlParameters
        );

        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];

        if (forEntity.getStatusCode() == HttpStatus.OK) {
            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "interfaces ");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));
            urlParameters.put("speed", speed);

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", cookies);
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X capability"
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} speed {speed}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            return responseGenerator(response);
        }
        return responseGenerator(forEntity);
    }

    private ResponseEntity responseGenerator(ResponseEntity response){
        if(response.getStatusCode() == HttpStatus.OK){
            return ResponseEntity.ok("OK");
        }else{
            String body = response.getBody().toString();
            String errorMessage = body.substring(body.lastIndexOf("<error>")+1,body.lastIndexOf("</error"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }
}
