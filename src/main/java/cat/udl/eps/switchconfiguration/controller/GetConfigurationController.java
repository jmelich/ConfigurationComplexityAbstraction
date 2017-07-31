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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by ubuntudesktop on 6/06/17.
 */
@Controller
public class GetConfigurationController {
    private final Logger logger = LoggerFactory.getLogger(GetConfigurationController.class);


    @Autowired private ConnectorRepository connectorRepository;

    @RequestMapping(value = "/connectors/{id}/availableSettings", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/

    public @ResponseBody Map<String, Object>  getAvailableSettings(@PathVariable("id") Long id) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException{

        logger.info("User Requested Available Speeds of Connector: "+String.valueOf(id));

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
        logger.info(cookies);

        if(forEntity.getStatusCode()== HttpStatus.OK){

            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "show interfaces");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie",cookies);
            headers.set("Content-Type",MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X capability"
            ResponseEntity<String> response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} capability", HttpMethod.GET, entity, String.class, urlParameters);

            String[] info = response.getBody().split("\n");

            String[] available = info[7].split("\\s+");

            Map<String, String> availableSettings = new HashMap<>();
            availableSettings.put("PortSpeed", available[6].concat("/Auto"));
            availableSettings.put("DuplexMode", available[7].concat("/Auto"));
            availableSettings.put("AdministrativeStatus", "enable/disable");

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show vlan"
            urlParameters.put("cmdCommand", "show vlan");
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand}", HttpMethod.GET, entity, String.class, urlParameters);
            logger.info(response.getBody());
            info = response.getBody().split("\n");
            String vlans ="";
            for(int i=7; i<info.length-5; i++){
                if(i==7){
                    vlans = vlans.concat(info[i].split("\\s+")[0]);
                }else{
                    vlans = vlans.concat("/" + info[i].split("\\s+")[0]);
                }
            }
            availableSettings.put("AvailableVLANs", vlans);


            logger.info(availableSettings.toString());

            //RETURN VALUES TO CLIENT
            Map<String,Object> returns = new HashMap<>();
            returns.put("AvailableSettings", availableSettings);
            return returns;
        }
        return null;
    }

    @RequestMapping(value = "/connectors/{id}/currentSettings", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/

    public @ResponseBody Map<String, Object>  getCurrentSettings(@PathVariable("id") Long id) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.info("User Requested Current Speeds of Connector: "+String.valueOf(id));

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
        logger.info(cookies);

        if(forEntity.getStatusCode()== HttpStatus.OK){
            //COLLECT AND MAP ALL RECEIVED DATA
            Map<String, String> currentSettings = new HashMap<>();

            //URL PARAMETERS TO SET TARGET EQUIPMENT
            urlParameters.put("cmdCommand", "show interfaces");
            urlParameters.put("password", equipment.getPassword());
            urlParameters.put("routerInStack", String.valueOf(equipment.getPositionInStack()));
            urlParameters.put("cardNumber", String.valueOf(card.getNumberOfCard()));
            urlParameters.put("portNumber", String.valueOf(port.getTitle()));

            //SET COOKIES TO THE HEADER
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie",cookies);
            headers.set("Content-Type",MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(headers);


            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X status"
            ResponseEntity<String> response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} status", HttpMethod.GET, entity, String.class, urlParameters);

            String[] info = response.getBody().split("\n");
            info = info[9].split("\\s+");
            currentSettings.put("AdministrativeStatus", info[2]);
            currentSettings.put("CurrentDuplex", info[8]);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X traffic"
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} traffic", HttpMethod.GET, entity, String.class, urlParameters);
            info = response.getBody().split("\n");
            try{
                currentSettings.put("InputBytes", info[7].split("\\s+")[3]);
            }catch (ArrayIndexOutOfBoundsException e){
                currentSettings.put("InputBytes", "0");
            }

            try{
                currentSettings.put("OutputBytes", info[7].split("\\s+")[5]);
            }catch(ArrayIndexOutOfBoundsException e){
                currentSettings.put("OutputBytes", "0");
            }

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show vlan"
            urlParameters.put("cmdCommand", "show vlan members port");
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber}", HttpMethod.GET, entity, String.class, urlParameters);
            logger.info(response.getBody());
            info = response.getBody().split("\n");
            String vlans ="";
            for(int i=7; i<info.length-5; i++){
                if(i==7){
                    vlans = vlans.concat(info[i].split("\\s+")[1]);
                }else{
                    vlans = vlans.concat("/" + info[i].split("\\s+")[1]);
                }
            }
            currentSettings.put("ConnectedToVLANs", vlans);

            Map<String,Object> returns = new HashMap<>();
            returns.put("ActuallySettings", currentSettings);
            return returns;
        }
        return null;
    }
}
