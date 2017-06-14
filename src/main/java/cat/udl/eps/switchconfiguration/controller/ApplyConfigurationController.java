package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import cat.udl.eps.switchconfiguration.domain.Port;
import cat.udl.eps.switchconfiguration.repository.ConnectorRepository;
import cat.udl.eps.switchconfiguration.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ubuntudesktop on 6/06/17.
 */
@Controller
public class ApplyConfigurationController {
    private final Logger logger = LoggerFactory.getLogger(ApplyConfigurationController.class);


    @Autowired private ConnectorRepository connectorRepository;

    @RequestMapping(value = "/connectors/{id}/availableSpeed", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/
    public @ResponseBody void getAvailableSpeeds(@PathVariable("id") Long id) {

        logger.info("User Requested Available Speeds of Connector: "+String.valueOf(id));

        Connector connector = connectorRepository.findOne(id);
        Equipment equipment = connector.getIsInEquipment();

        int usedPortInEquipment = connector.getEquipmentPort();
        String equipmentIP = equipment.getIP();
        String username = equipment.getUsername();
        String password = equipment.getPassword();

        RestTemplate restTemplate = new RestTemplate();

        String URL = String.format("https://%s/auth/?&username=%s&password=%s",equipmentIP,username,password);

        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            //make call to get available speeds
            URL = String.format("https://%s/cli/aos?&cmd=show+interfaces+%s",equipmentIP,usedPortInEquipment);
            response = restTemplate.getForEntity(URL, String.class);
            logger.info(response.getBody());
            //return response to client
        }else{
            logger.error("Cannot locate equipment or username and password are wrong");
        }


    }


    @RequestMapping(value = "/connectors/{port}/enablePort", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/
    public @ResponseBody void enablePort(@PathVariable("port") Long port) {

        logger.info("User Requested to Enable a Port: "+String.valueOf(port));

        Connector connector = connectorRepository.findOne(port);
        Equipment equipment = connector.getIsInEquipment();

        int usedPortInEquipment = connector.getEquipmentPort();
        String equipmentIP = equipment.getIP();
        String username = equipment.getUsername();
        String password = equipment.getPassword();

        RestTemplate restTemplate = new RestTemplate();

        String URL = String.format("https://%s/auth/?&username=%s&password=%s",equipmentIP,username,password);

        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            //make call to get available speeds
            URL = String.format("https://%s/cli/aos?&cmd=interfaces+%s+admin-state+enable",equipmentIP,usedPortInEquipment);
            response = restTemplate.getForEntity(URL, String.class);
            logger.info(response.getBody());
            //return response to client
        }else{
            logger.error("Cannot locate equipment or username and password are wrong");
        }
    }


    @RequestMapping(value = "/connectors/{port}/disablePort", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    /*public @ResponseBody UserDetails getCurrentUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }*/
    public @ResponseBody void disablePort(@PathVariable("port") Long port) {

        logger.info("User Requested to Disable a Port: "+String.valueOf(port));

        Connector connector = connectorRepository.findOne(port);
        Equipment equipment = connector.getIsInEquipment();

        int usedPortInEquipment = connector.getEquipmentPort();
        String equipmentIP = equipment.getIP();
        String username = equipment.getUsername();
        String password = equipment.getPassword();

        RestTemplate restTemplate = new RestTemplate();

        String URL = String.format("https://%s/auth/?&username=%s&password=%s",equipmentIP,username,password);

        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            //make call to get available speeds
            URL = String.format("https://%s/cli/aos?&cmd=interfaces+%s+admin-state+disable",equipmentIP,usedPortInEquipment);
            response = restTemplate.getForEntity(URL, String.class);
            logger.info(response.getBody());
            //return response to client
        }else{
            logger.error("Cannot locate equipment or username and password are wrong");
        }
    }
}
