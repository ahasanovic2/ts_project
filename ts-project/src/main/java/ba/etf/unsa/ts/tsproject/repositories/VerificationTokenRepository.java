package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.User;
import ba.etf.unsa.ts.tsproject.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    Optional<VerificationToken> getVerificationTokenByUser(User user);
    void deleteVerificationTokenByUser(User user);
    void removeVerificationTokenByUser(User user);
}