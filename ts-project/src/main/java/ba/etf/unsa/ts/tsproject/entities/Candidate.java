package ba.etf.unsa.ts.tsproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"firstName", "lastName"})
)
@Getter
@Setter
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "This field cannot be null")
    @Size(min = 1, message = "This field must contain at least 1 character")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    private String firstName;

    @NotNull(message = "This field cannot be null")
    @Size(min = 1, message = "This field must contain at least 1 character")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    private String lastName;

    @NotNull(message = "This field cannot be null")
    @Size(min = 20, message = "This field must contain at least 20 characters.")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="electionId")
    private Election election;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"firstName\":\"" + firstName + "\"," +
                "\"lastName\":\"" + lastName + "\"," +
                "\"description\":\"" + description.replace("\"", "\\\"") + "\"" +
                "}";
    }

}
