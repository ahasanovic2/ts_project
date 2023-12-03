package ba.etf.unsa.ts.tsproject.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Passwords {
    private String oldPassword;
    private String newPassword;
}
