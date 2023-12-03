package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.entities.PollingStation;
import ba.etf.unsa.ts.tsproject.entities.User;
import ba.etf.unsa.ts.tsproject.exception.ErrorDetails;
import ba.etf.unsa.ts.tsproject.repositories.PollingStationRepository;
import ba.etf.unsa.ts.tsproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollingStationService {

    private final PollingStationRepository pollingStationRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    public ResponseEntity<?> getPollingStations() {
        List<PollingStation> pollingStations = pollingStationRepository.findAll();
        if (pollingStations.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDetails(LocalDateTime.now(),"pollingstations","No polling stations found!"));
        return ResponseEntity.status(HttpStatus.OK).body(pollingStations);
    }


    public ResponseEntity<?> addPollingStation(PollingStation pollingStation) {
        pollingStationRepository.save(pollingStation);
        return ResponseEntity.status(HttpStatus.OK).body(pollingStation);
    }

    public ResponseEntity<?> getPollingStationForUser() {
        Integer userId = userService.getUserIdFromAuthentication();

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"user","User ID not present in database"));
        }
        User user = optionalUser.get();
        PollingStation pollingStation = user.getPollingStation();
        if (pollingStation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PollingStation not found");
        }
        return ResponseEntity.ok(pollingStation);
    }

    public ResponseEntity<?> getPollingStationByName(String pollingStationName) {
        Optional<PollingStation> optionalPollingStation = pollingStationRepository.findByName(pollingStationName);
        if (optionalPollingStation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"name","PS not present in database"));
        }
        return ResponseEntity.ok(optionalPollingStation.get());
    }

    public ResponseEntity getPollingStationByUserId(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"user","User ID not present in database"));
        }
        User user = optionalUser.get();
        Optional<PollingStation> optionalPollingStation = pollingStationRepository.getPollingStationByUsers(user);
        if (optionalPollingStation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"user","User has no PS"));
        }
        return ResponseEntity.ok(optionalPollingStation.get());
    }
}