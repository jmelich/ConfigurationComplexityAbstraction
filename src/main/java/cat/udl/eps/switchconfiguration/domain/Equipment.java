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
public class Equipment extends UriEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private String IP;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    //@NotBlank
    private int positionInStack;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private EquipmentRoom isInEquipmentRoom;

    @OneToMany(mappedBy = "isInEquipment", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Card> cards = new ArrayList<>();

}
