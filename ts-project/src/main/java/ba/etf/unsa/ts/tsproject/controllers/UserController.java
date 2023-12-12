package ba.etf.unsa.ts.tsproject.controllers;

import ba.etf.unsa.ts.tsproject.auth.RegisterRequest;
import ba.etf.unsa.ts.tsproject.email.RegistrationCompleteEvent;
import ba.etf.unsa.ts.tsproject.entities.*;
import ba.etf.unsa.ts.tsproject.repositories.PollingStationRepository;
import ba.etf.unsa.ts.tsproject.repositories.UserRepository;
import ba.etf.unsa.ts.tsproject.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PollingStationRepository pollingStationRepository;
    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final Environment env;

    @GetMapping("")
    public ResponseEntity getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/id")
    public ResponseEntity<Integer> getId() {
        Integer userId = userService.getUserIdFromAuthentication();
        return ResponseEntity.status(HttpStatus.OK).body(userId);
    }

    @PostMapping("/{userId}/pollingStation/{pollingStationId}")
    public ResponseEntity<String> setPollingStation(@PathVariable Integer userId, @PathVariable Integer pollingStationId) {
        return userService.setPollingStation(userId,pollingStationId);
    }

    @PostMapping("/{userId}/pollingStation?name={pollingStationName}")
    public ResponseEntity<String> setPStoUser(@PathVariable Integer userId, @PathVariable String pollingStationName) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<PollingStation> optionalPollingStation = pollingStationRepository.findByName(pollingStationName);
        if (optionalUser.isEmpty() || optionalPollingStation.isEmpty())
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User or Polling station not found");
        User user = optionalUser.get();
        PollingStation pollingStation = optionalPollingStation.get();

        user.setPollingStation(pollingStation);
        pollingStation.addUser(user);

        userRepository.save(user);
        pollingStationRepository.save(pollingStation);
        return new ResponseEntity<>("{\"text\":\"Added polling station\"" + pollingStation.getId() + " to user " + user.getId() + "}", HttpStatus.OK);
    }

    @PostMapping("/pollingStation")
    public ResponseEntity<String> setPS(@RequestParam String name) {
        return setPStoUser(getId().getBody(), name);
    }

    private final ApplicationEventPublisher publisher;

    @PostMapping("/create-admin")
    public ResponseEntity createAdmin(@RequestBody @Valid RegisterRequest registerRequest, final HttpServletRequest request) {
        ResponseEntity responseEntity =  userService.createAdmin(registerRequest);
        User user = null;
        try {
            user = (User) responseEntity.getBody();
        } catch (ClassCastException e) {
            return responseEntity;
        }
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return ResponseEntity.ok("Success! Admin with email " + user.getEmail() + " must verify email before logging to application.");
    }

    @GetMapping("/admins")
    public ResponseEntity getAdmins(HttpServletRequest httpServletRequest) {
        return userService.getAdmins(httpServletRequest);
    }

    @DeleteMapping("/delete-admin")
    public ResponseEntity deleteAdmin(@RequestParam String email) {
        return userService.deleteAdmin(email);
    }

    @PutMapping("/change-password")
    public ResponseEntity changePassword(@RequestBody Passwords passwords) {
        return userService.changePassword(passwords);
    }

    @PostMapping("/front-logout")
    public ResponseEntity frontLogout(@RequestParam String email) {
        return userService.frontLogout(email);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }

}