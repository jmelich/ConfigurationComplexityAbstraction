package cat.udl.eps.switchconfiguration.utils;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ubuntu on 1/08/17.
 */
@XmlRootElement
@Data
public class CurrentSettingsResponse {
    private String currentDuplex;
    private String inputBytes;
    private String outputBytes;
    private String portSpeed;
    private String administrativeStatus;
    private String connectedToVLANs;
}
