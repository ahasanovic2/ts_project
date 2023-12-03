package ba.etf.unsa.ts.tsproject.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"name", "electionId"})
)
@Getter
@Setter
public class Lista {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "This field cannot bec null")
    @Size(min = 2, message = "This field must be at least 2 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    private String name;

    @NotNull(message = "This field cannot be null")
    @Size(min = 20, message = "This field must contain at least 20 characters.")
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "electionId")
    private Election election;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"name\":\"" + name + "\"," +
                "\"description\":" + description +
                "}";
    }

}