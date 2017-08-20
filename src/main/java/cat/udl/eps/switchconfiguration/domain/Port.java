package cat.udl.eps.switchconfiguration.domain;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;


@Entity
@Data
public class Port  extends UriEntity<Long>   {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private int portNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIdentityReference(alwaysAsId = true)
    private Card isInCard;

    @OneToOne(fetch=FetchType.EAGER, mappedBy="connectedTo", cascade = {CascadeType.MERGE,  CascadeType.REFRESH, CascadeType.DETACH})
    @JsonIdentityReference(alwaysAsId = true)
    private Connector connector;

}
