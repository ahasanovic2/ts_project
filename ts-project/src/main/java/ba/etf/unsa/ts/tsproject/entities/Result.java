package ba.etf.unsa.ts.tsproject.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Min(value = 0, message = "Vote count cannot be less than zero")
    @Digits(integer = 15, fraction = 0, message = "Vote count must be a number")
    private int voteCount;

    private String candidateFirstName;
    private String candidateLastName;
    private String pollingStationName;
    private String listName;
    private String electionName;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"voteCount\":" + voteCount + "," +
                "\"candidateFirstName\":\"" + candidateFirstName + "\"," +
                "\"candidateLastName\":\"" + candidateLastName + "\"," +
                "\"pollingStationName\":\"" + pollingStationName + "\"," +
                "\"listName\":\"" + listName + "\"," +
                "\"electionName\":\"" + electionName + "\"" +
                "}";
    }

}