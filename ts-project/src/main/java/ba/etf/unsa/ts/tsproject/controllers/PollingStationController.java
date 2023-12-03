package ba.etf.unsa.ts.tsproject.controllers;

import ba.etf.unsa.ts.tsproject.entities.PollingStation;
import ba.etf.unsa.ts.tsproject.services.PollingStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pollingStations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PollingStationController {

    private final PollingStationService pollingStationService;

    @GetMapping("")
    public ResponseEntity<?> getPollingStations() {
        return pollingStationService.getPollingStations();
    }

    @PostMapping("/create")
    public ResponseEntity<?> addPollingStation(@Valid @RequestBody PollingStation pollingStation) {
        return pollingStationService.addPollingStation(pollingStation);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getPollingStationForUser() {
        return pollingStationService.getPollingStationForUser();
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<?> getPollingStationByName(@RequestParam String pollingStationName) {
        return pollingStationService.getPollingStationByName(pollingStationName);
    }

    @GetMapping("/user/get-by-id")
    public ResponseEntity getPollingStationByUserId(@RequestParam Integer userId) {
        return pollingStationService.getPollingStationByUserId(userId);
    }
}