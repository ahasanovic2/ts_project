package ba.etf.unsa.ts.tsproject.controllers;

import ba.etf.unsa.ts.tsproject.auth.AuthenticationRequest;
import ba.etf.unsa.ts.tsproject.auth.RegisterRequest;
import ba.etf.unsa.ts.tsproject.email.RegistrationCompleteEvent;
import ba.etf.unsa.ts.tsproject.entities.User;
import ba.etf.unsa.ts.tsproject.entities.VerificationToken;
import ba.etf.unsa.ts.tsproject.repositories.VerificationTokenRepository;
import ba.etf.unsa.ts.tsproject.services.AuthenticationService;
import ba.etf.unsa.ts.tsproject.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;



    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest, final HttpServletRequest request) {
        User user;
        ResponseEntity response = authenticationService.register(registerRequest);
        try {
            user = (User) response.getBody();
        } catch (ClassCastException e) {
            return response;
        }
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return ResponseEntity.ok("Success!  Please, check your email for to complete your registration");
    }

    @PostMapping("/login")
    public ResponseEntity register( @RequestBody AuthenticationRequest request) throws Exception {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PermitAll
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam Map<String, String> request){
        String email = request.get("email");
        return new ResponseEntity<>(authenticationService.forgotPassword(email), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> setPassword(@RequestParam String email, @RequestHeader String newPassword){
        return new ResponseEntity<>(authenticationService.setPassword(email, newPassword), HttpStatus.OK);
    }


    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = verificationTokenRepository.findByToken(token);
        if (theToken.getUser().getIsEnabled()){
            return "This account has already been verified, please, login.";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    @GetMapping("/check-validation")
    public ResponseEntity checkValidation(@RequestParam("email") String email, HttpServletRequest request) {
        return authenticationService.checkValidation(email,request);
    }



    private String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}