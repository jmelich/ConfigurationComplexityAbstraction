package cat.udl.eps.switchconfiguration.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.atteo.evo.inflector.English;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Users") //Avoid collision with system table User in Postgres
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="uri")
public abstract class User implements UserDetails {
    @Id
    private String username;

    private String password;

    private String uri;

    @Override
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username=username; }

    public String getUri() {
        return "/" + English.plural(StringUtils.uncapitalize(this.getClass().getSimpleName())) + "/" + username;
    }

    @Override
    @JsonIgnore
    public String getPassword(){ return password; }

    public void setPassword(String password){ this.password=password; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
