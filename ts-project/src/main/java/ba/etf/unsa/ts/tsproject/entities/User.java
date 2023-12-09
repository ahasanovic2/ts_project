package ba.etf.unsa.ts.tsproject.entities;

import ba.etf.unsa.ts.tsproject.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    private String firstname;
    private String lastname;


    @Email
    private String email;

    @Size(min = 8, message = "Password must contain at least 8 characters.")
    private String password;


    private Boolean isEnabled = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Nullable
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "polling_station_id")
    private PollingStation pollingStation;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"," +
                "\"first_name\":\"" + firstname + "\"," +
                "\"last_name\":\"" + lastname + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"role\":\"" + (role != null ? role.name() : "") + "\"," +
                "\"polling_station\":\"" + (pollingStation != null ? pollingStation.getId() : 0) + "\"" +
                "}";
    }


}