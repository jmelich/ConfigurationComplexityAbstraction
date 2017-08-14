package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Card;
import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import cat.udl.eps.switchconfiguration.domain.Port;
import cat.udl.eps.switchconfiguration.repository.ConnectorRepository;
import cat.udl.eps.switchconfiguration.utils.AvailableSettingsResponse;
import cat.udl.eps.switchconfiguration.utils.CurrentSettingsResponse;
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


@Controller
public class GetConfigurationController {
    private final Logger logger = LoggerFactory.getLogger(GetConfigurationController.class);


    @Autowired private ConnectorRepository connectorRepository;

    @RequestMapping(value = "/connectors/{id}/availableSettings", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody
    AvailableSettingsResponse getAvailableSettings(@PathVariable("id") Long id)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException{

        logger.info("User Requested Available Speeds of Connector: "+String.valueOf(id));

        AvailableSettingsResponse availableSettingsResponse = new AvailableSettingsResponse();

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
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} capability",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            String[] info = response.getBody().split("\n");

            String[] available = info[7].split("\\s+");

            //Map<String, String> availableSettings = new HashMap<>();

            availableSettingsResponse.setPortSpeed(available[6].concat("/Auto").replace("1G","1000"));
            availableSettingsResponse.setDuplexMode(available[7].concat("/Auto"));
            availableSettingsResponse.setAdministrativeStatus("enable/disable");


            //MAKE REQUEST WITH SPECIFIED COMMAND: "show vlan"
            urlParameters.put("cmdCommand", "show vlan");

            response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            info = response.getBody().split("\n");
            String vlans ="";
            for(int i=7; i<info.length-5; i++){
                if(i==7){
                    vlans = vlans.concat(info[i].split("\\s+")[0]);
                }else{
                    vlans = vlans.concat("/" + info[i].split("\\s+")[0]);
                }
            }
            availableSettingsResponse.setAvailableVLANs(vlans);

            //RETURN VALUES TO CLIENT
            return availableSettingsResponse;
        }
        return null;
    }

    @RequestMapping(value = "/connectors/{id}/currentSettings", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("isAuthenticated()")

    public @ResponseBody CurrentSettingsResponse  getCurrentSettings(@PathVariable("id") Long id)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        logger.info("User Requested Current Speeds of Connector: "+String.valueOf(id));

        CurrentSettingsResponse currentSettingsResponse = new CurrentSettingsResponse();

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

        if(forEntity.getStatusCode()== HttpStatus.OK){
            //COLLECT AND MAP ALL RECEIVED DATA

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
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} status",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            String[] info = response.getBody().split("\n");
            info = info[9].split("\\s+");
            if(info[2].equals("en")){
                currentSettingsResponse.setAdministrativeStatus("enable");
            }else{
                currentSettingsResponse.setAdministrativeStatus("disable");
            }
            currentSettingsResponse.setCurrentDuplex(info[8]);
            currentSettingsResponse.setPortSpeed(info[7]);
            if(currentSettingsResponse.getPortSpeed() == "1G")
                currentSettingsResponse.setPortSpeed("1000");

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X traffic"
            response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} traffic",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            info = response.getBody().split("\n");
            try{
                currentSettingsResponse.setInputBytes(info[7].split("\\s+")[3]);
            }catch (ArrayIndexOutOfBoundsException e){
                currentSettingsResponse.setInputBytes("0");
            }

            try{
                currentSettingsResponse.setOutputBytes( info[7].split("\\s+")[5]);
            }catch(ArrayIndexOutOfBoundsException e){
                currentSettingsResponse.setOutputBytes("0");
            }

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show vlan"
            urlParameters.put("cmdCommand", "show vlan members port");
            response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            info = response.getBody().split("\n");
            String vlans ="";
            for(int i=7; i<info.length-5; i++){
                if(i==7){
                    vlans = vlans.concat(info[i].split("\\s+")[1]);
                }else{
                    vlans = vlans.concat("/" + info[i].split("\\s+")[1]);
                }
            }
            currentSettingsResponse.setConnectedToVLANs(vlans);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show running-directory"
            urlParameters.put("cmdCommand", "show running-directory");
            response = restTemplate.exchange(
                    "http://{equipmentIP}/cli/aos?cmd={cmdCommand}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    urlParameters
            );

            String runningDirectory = response.getBody()
                    .split("\n")[10]
                    .split(":")[1]
                    .replaceAll(",$", "")
                    .trim();

            currentSettingsResponse.setRunningDirectory(runningDirectory);

            return currentSettingsResponse;
        }
        return null;
    }
}
