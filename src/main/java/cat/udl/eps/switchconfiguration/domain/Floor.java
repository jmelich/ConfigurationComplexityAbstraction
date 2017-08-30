package cat.udl.eps.switchconfiguration.domain;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class Floor  extends UriEntity<Long>  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @Column(length = 5 * 1024 * 1024) // 5MB
    private String picture;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Building isInBuilding;

    @OneToMany(mappedBy = "isInFloor", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<EquipmentRoom> equipmentRooms = new ArrayList<>();

    @OneToMany(mappedBy = "isInFloor", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Connector> connectors = new ArrayList<>();

}
