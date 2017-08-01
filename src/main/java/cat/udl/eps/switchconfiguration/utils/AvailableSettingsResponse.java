package cat.udl.eps.switchconfiguration.utils;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ubuntu on 1/08/17.
 */
@XmlRootElement
@Data
public class AvailableSettingsResponse {
    private String PortSpeed;
    private String DuplexMode;
    private String AdministrativeStatus;
    private String AvailableVLANs;
}
