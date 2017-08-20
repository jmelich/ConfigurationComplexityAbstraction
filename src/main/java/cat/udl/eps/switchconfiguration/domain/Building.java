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
public class Building extends UriEntity<Long>  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    //@OneToMany(fetch = FetchType.EAGER, cascade = ALL)
    @OneToMany(mappedBy = "isInBuilding", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Floor> floors = new ArrayList<>();

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Campus isInCampus;

}
