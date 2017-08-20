package cat.udl.eps.switchconfiguration.domain;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;


@Entity
@Data
public class Connector extends UriEntity<Long>   {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private double latitude;

    private double longitude;

    private int equipmentPort;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Equipment isInEquipment;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Floor isInFloor;

    @OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIdentityReference(alwaysAsId = true)
    private Port connectedTo;

}
