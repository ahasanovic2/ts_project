package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.auth.RegisterRequest;
import ba.etf.unsa.ts.tsproject.authconfig.JwtService;
import ba.etf.unsa.ts.tsproject.entities.*;
import ba.etf.unsa.ts.tsproject.exception.ErrorDetails;
import ba.etf.unsa.ts.tsproject.repositories.PasswordResetTokenRepository;
import ba.etf.unsa.ts.tsproject.repositories.PollingStationRepository;
import ba.etf.unsa.ts.tsproject.repositories.UserRepository;
import ba.etf.unsa.ts.tsproject.repositories.VerificationTokenRepository;
import ba.etf.unsa.ts.tsproject.token.Token;
import ba.etf.unsa.ts.tsproject.token.TokenRepository;
import ba.etf.unsa.ts.tsproject.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PollingStationRepository pollingStationRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public ResponseEntity<String> setPollingStation(Integer userId, Integer pollingStationId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<PollingStation> optionalPollingStation = pollingStationRepository.findById(pollingStationId);

        if (optionalUser.isEmpty() || optionalPollingStation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User or Polling station not found");
        }

        User user = optionalUser.get();
        PollingStation pollingStation = optionalPollingStation.get();

        user.setPollingStation(pollingStation);
        pollingStation.addUser(user);

        userRepository.save(user);
        pollingStationRepository.save(pollingStation);
        return ResponseEntity.ok("Added polling station " + pollingStation.getId() + " to user " + user.getId());
    }

    public ResponseEntity getUsers() {
        List<User> users = userRepository.getUsersByRole(Role.USER);
        if (users.isEmpty() || users == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"admins","No admins found"));
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    public Integer getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ResponseEntity createAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(LocalDateTime.now(),"email","Email already present in database"));
        }



            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .isEnabled(false)
                    .role(request.getRole())
                    .build();
            if (user.getRole() == Role.SUPERADMIN)
                user.setIsEnabled(true);
            var savedUser = userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);
            return ResponseEntity.ok(user);

    }

    private void saveUserToken(User savedUser, String jwtToken) {
        var token = Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Transactional
    public ResponseEntity deleteAdmin(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            verificationTokenRepository.removeVerificationTokenByUser(user.get());
            tokenRepository.deleteTokensByUser(user.get());
            userRepository.delete(user.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted admin with email: " + email);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"admin","Admin with email " + email + " not found."));
    }

    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        verificationTokenRepository.save(verificationToken);
    }

    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(token);
            return "Token already expired";
        }
        user.setIsEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    public ResponseEntity getAdmins(HttpServletRequest httpServletRequest) {
        List<User> admins = userRepository.getUsersByRole(Role.ADMIN);
        if (admins.isEmpty() || admins == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"admins","No admins found"));
        return ResponseEntity.status(HttpStatus.OK).body(admins);
    }

    public ResponseEntity changePassword(Passwords passwords) {
        Integer userId = getUserIdFromAuthentication();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty() || optionalUser == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"id","No user found with that ID"));
        User user = optionalUser.get();
        if (!passwordEncoder.matches(passwords.getOldPassword(),user.getPassword()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(), "password","Old password is not correct"));
        user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("{\"status\": \"success\", \"message\": \"Successfully changed password to user\"}");
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    public ResponseEntity frontLogout(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser == null || optionalUser.isEmpty())
            return ResponseEntity.status(404).body(new ErrorDetails(LocalDateTime.now(), "email", "No user found by that email"));
        User user = optionalUser.get();
        List<Token> tokens = user.getTokens();
        for (var token: tokens) {
            token.setExpired(true);
            token.setRevoked(true);
        }
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Success\"}");
    }


}