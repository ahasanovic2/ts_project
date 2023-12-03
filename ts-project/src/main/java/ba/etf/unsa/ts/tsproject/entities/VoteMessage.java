package ba.etf.unsa.ts.tsproject.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class VoteMessage {
    private Integer id;
    private Integer voterId;
    private Integer electionId;
    private Integer candidateId;
    private Integer listaId;
    private String timestamp;
    private String token;
}