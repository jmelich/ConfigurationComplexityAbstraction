package cat.udl.eps.switchconfiguration.controller;

import cat.udl.eps.switchconfiguration.domain.Connector;
import cat.udl.eps.switchconfiguration.domain.Equipment;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public @ResponseBody void getAvailableSpeeds(@PathVariable("id") Long id) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException{

        logger.info("User Requested Available Speeds of Connector: "+String.valueOf(id));

        Connector connector = connectorRepository.findOne(id);
        Equipment equipment = connector.getIsInEquipment();

        int usedPortInEquipment = connector.getEquipmentPort();
        String equipmentIP = equipment.getIP();
        String username = equipment.getUsername();
        String password = equipment.getPassword();

        /*CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

        //RestTemplate restTemplate = new RestTemplate(Collections.singletonList(new GsonHttpMessageConverter()));
        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(1000)
                .setMaxConnPerRoute(1000)
                .build();*/
        //restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        //RestTemplate restTemplate = new org.springframework.web.client.RestTemplate(new HttpComponentsClientHttpRequestFactory());

        /*TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        String URL = String.format("http://%s/auth/?&username=%s&password=%s",equipmentIP,username,password);
        logger.info(URL);

        ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
        logger.info(response.toString());

        logger.info(response.getHeaders().get("Set-Cookie").get(0));

        if(response.getStatusCode() == HttpStatus.OK){
            //make call to get available speeds
            URL = String.format("http://%s/cli/aos?&cmd=show+interfaces+1/1/%s",equipmentIP,usedPortInEquipment);
            logger.info(URL);
            try{
                response = restTemplate.getForEntity(URL, String.class);
            }catch (HttpClientErrorException e){
                logger.error(response.toString());
            }

            logger.info(response.toString());
            //return response to client
        }else{
            logger.error("Cannot locate equipment or username and password are wrong");
        }*/

        //first request



        RestTemplate template = new RestTemplate();
        ResponseEntity<String> forEntity = template.getForEntity("http://192.168.1.1/auth/?&username=admin&password=switch", String.class);
        String cookies = forEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        logger.info(cookies);

        if(forEntity.getStatusCode()== HttpStatus.OK){

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie",cookies);
            //headers.set("Accept", "application/json");
            //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            //headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Content-Type",MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange("http://192.168.1.1/cli/aos?cmd=show interfaces 1/1/1", HttpMethod.GET, entity, String.class);

            logger.info(response.toString());
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
