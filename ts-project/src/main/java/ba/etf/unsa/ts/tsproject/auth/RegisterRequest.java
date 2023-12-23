package ba.etf.unsa.ts.tsproject.auth;

import ba.etf.unsa.ts.tsproject.entities.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.Validator;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    @Email
    private String email;
    @Size(min = 8, message = "Password must contain at least 8 characters.")
    private String password;
    private Role role;


}