package cat.udl.eps.switchconfiguration.domain;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
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
    private List<Dealer> dealers = new ArrayList<>();

    @OneToMany(mappedBy = "isInFloor", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Connector> connectors = new ArrayList<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @ReadOnlyProperty
    private ZonedDateTime dateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @LastModifiedDate
    private ZonedDateTime lastModified;




}
