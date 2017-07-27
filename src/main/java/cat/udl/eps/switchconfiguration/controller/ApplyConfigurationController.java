package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Card;
import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
import cat.udl.eps.switchconfiguration.domain.Port;
import cat.udl.eps.switchconfiguration.repository.ConnectorRepository;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public @ResponseBody Map<String, Object>  getAvailableSpeeds(@PathVariable("id") Long id) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException{

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
            String[] actually = info[8].split("\\s+");

            Map<String, String> availableSettings = new HashMap<>();
            Map<String, String> actuallySettings = new HashMap<>();
            availableSettings.put("PortSpeed", available[6]);
            availableSettings.put("DuplexMode", available[7].concat("/Auto"));

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X status"
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} status", HttpMethod.GET, entity, String.class, urlParameters);

            info = response.getBody().split("\n");
            actuallySettings.put("AdministrativeStatus", info[9].split("\\s+")[2]);

            //MAKE REQUEST WITH SPECIFIED COMMAND: "show interfaces X/X/X traffic"
            response = restTemplate.exchange("http://{equipmentIP}/cli/aos?cmd={cmdCommand} {routerInStack}/{cardNumber}/{portNumber} traffic", HttpMethod.GET, entity, String.class, urlParameters);
            info = response.getBody().split("\n");
            actuallySettings.put("InputBytes", info[7].split("\\s+")[3]);
            actuallySettings.put("OutputBytes", info[7].split("\\s+")[5]);

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
            returns.put("ActuallySettings", actuallySettings);
            return returns;
        }
        return null;
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
