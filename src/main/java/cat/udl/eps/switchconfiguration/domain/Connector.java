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
public class Connector extends UriEntity<Long>   {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    private long latitude;

    private long longitude;

    private int equipmentPort;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Equipment isInEquipment;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Floor isInFloor;

    @OneToOne(fetch=FetchType.EAGER)
    @JsonIdentityReference(alwaysAsId = true)
    private Port connectedTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @ReadOnlyProperty
    private ZonedDateTime dateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @LastModifiedDate
    private ZonedDateTime lastModified;




}
