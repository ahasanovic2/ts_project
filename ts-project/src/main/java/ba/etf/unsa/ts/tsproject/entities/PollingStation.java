package ba.etf.unsa.ts.tsproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
public class PollingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "This field cannot be null")
    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    private String name;

    @NotNull(message = "This field cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    private String address;

    @NotBlank(message = "Entitet cannot be blank")
    @Pattern(regexp = "^(RepublikaSrpska|FederacijaBiH)$")
    private String entitet;
    @Nullable
    @Pattern(regexp = "^((Unsko Sanski)|(Posavski)|(Tuzlanski)|(Zenicko Dobojski)|(Bosansko Podrinjski)|(Srednjobosanski)|(Hercegovacko neretvanski)|(Zapadnohercegovacki)|(Sarajevo)|(Kanton 10))$", message = "Kanton must be one of predefined values")
    private String kanton;

    @NotNull(message = "Opcina cannot be null")
    @NotBlank(message = "Opcina cannot be blank")
    private String opcina;

    public PollingStation(String name, String address, String entitet, String kanton, String opcina) {
        this.name = name;
        this.address = address;
        this.entitet = entitet;
        this.kanton = kanton;
        this.opcina = opcina;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(name = "polling_station_election",
            joinColumns = @JoinColumn(name = "polling_station_id"),
            inverseJoinColumns = @JoinColumn(name = "election_id"))
    private Set<Election> elections = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "pollingStation")
    private List<User> users = new ArrayList<>();

    @Override
    public String toString() {
        return "PollingStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", entitet='" + entitet + '\'' +
                ", kanton='" + kanton + '\'' +
                ", opcina='" + opcina + '\'' +
                ", elections=" + elections +
                '}';
    }

    public void addElections(Election election) {
        this.elections.add(election);
    }

    public void addUser(User voter) {
        this.users.add(voter);
    }
}