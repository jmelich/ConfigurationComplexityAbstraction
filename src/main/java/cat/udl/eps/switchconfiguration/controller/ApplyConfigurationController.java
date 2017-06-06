package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
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
        String username = "";
        String password = "";

        RestTemplate restTemplate = new RestTemplate();

        String URL = String.format("https://%s/auth/?&username=%s&password=%s",equipmentIP,username,password);

        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            //do something
        }


    }
}
