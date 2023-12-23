package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.auth.AuthenticationRequest;
import ba.etf.unsa.ts.tsproject.auth.AuthenticationResponse;
import ba.etf.unsa.ts.tsproject.auth.RegisterRequest;
import ba.etf.unsa.ts.tsproject.authconfig.JwtService;
import ba.etf.unsa.ts.tsproject.email.EmailSenderService;
import ba.etf.unsa.ts.tsproject.entities.Role;
import ba.etf.unsa.ts.tsproject.entities.User;
import ba.etf.unsa.ts.tsproject.exception.ErrorDetails;
import ba.etf.unsa.ts.tsproject.repositories.UserRepository;
import ba.etf.unsa.ts.tsproject.token.Token;
import ba.etf.unsa.ts.tsproject.token.TokenRepository;
import ba.etf.unsa.ts.tsproject.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final TokenRepository tokenRepository;

    public ResponseEntity register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(LocalDateTime.now(),"email","Email already present in database"));
        }
        if (request.getRole() == Role.SUPERADMIN && userRepository.getUsersByRole(Role.SUPERADMIN).size() > 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(LocalDateTime.now(),"role","You cannot add superadmin"));
        }
        if (request.getRole() == Role.ADMIN)
            return ResponseEntity.status((HttpStatus.CONFLICT)).body(new ErrorDetails(LocalDateTime.now(),"role","Only superadmin can add admin"));
        if(request.getPassword().matches(".*[A-Z].*") && request.getPassword().matches(".*[a-z].*") && request.getPassword().matches(".*\\d.*") && request.getPassword().length()>=8) {
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
        else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(LocalDateTime.now(),"password","Password must contain at least one uppercase symbol, one lowercase symbol, one number and password must be at least 8 characters long"));
        }

    }


    public ResponseEntity authenticate(AuthenticationRequest request) throws Exception {
        if (!userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"email","Email not present in database"));
        }
        if (!userRepository.findByEmail(request.getEmail()).get().getIsEnabled())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"email","Email not verified"));


        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        try {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDetails(LocalDateTime.now(),"password","Wrong password"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);


            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build());
        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"password",ex.getMessage()));
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
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

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail =jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseEntity checkValidation(String email, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"email","User not found"));
        }
        User user = optionalUser.get();
        if (!user.getIsEnabled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"verification","User not verified"));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("{\"status\": \"User is found and verified!\"}");
        }
    }

    public ResponseEntity checkExpiration(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String stringToken = authHeader.substring(7);
            Optional<Token> optionalToken = tokenRepository.findByToken(stringToken);
            if (optionalToken == null || optionalToken.isEmpty())
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NOTOK");
            Token token = optionalToken.get();
            if (token.expired || token.revoked)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NOTOK");
            return ResponseEntity.status(HttpStatus.OK).body("OK");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NOTOK");
    }


    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found" + email));

        // Generate a one-time password
        SecureRandom random = new SecureRandom();

        // Generate a random uppercase letter
        char randomUppercaseLetter = (char) ('A' + random.nextInt(26));

        // Create the one-time password with an additional uppercase letter
        String oneTimePassword = new BigInteger(50, random).toString(32) + randomUppercaseLetter;

        // Shuffle the OTP to mix the uppercase letter into it
        List<Character> chars = oneTimePassword.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
        Collections.shuffle(chars);
        oneTimePassword = chars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        // Encode and store the one-time password in the database
        String encodedOneTimePassword = passwordEncoder.encode(oneTimePassword);
        user.setPassword(encodedOneTimePassword);
        userRepository.save(user);

        try {
            emailSenderService.sendSetPasswordEmail(email, oneTimePassword);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "Please check your email to set a new password!";
    }


}