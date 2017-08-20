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
public class Card  extends UriEntity<Long>  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    //@NotBlank
    private int numberOfCard;
    //@NotBlank
    private int numberOfPorts;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Equipment isInEquipment;

    @OneToMany(mappedBy = "isInCard", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Port> ports= new ArrayList<>();

    public void addPort(Port p){
        this.ports.add(p);
    }


}
