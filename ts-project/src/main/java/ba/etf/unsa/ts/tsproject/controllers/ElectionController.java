package ba.etf.unsa.ts.tsproject.controllers;

import ba.etf.unsa.ts.tsproject.entities.Candidate;
import ba.etf.unsa.ts.tsproject.entities.Election;
import ba.etf.unsa.ts.tsproject.entities.Lista;
import ba.etf.unsa.ts.tsproject.services.ElectionService;
import com.google.api.Http;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/elections")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ElectionController {

    private final ElectionService electionService;

    @GetMapping("")
    public ResponseEntity getElections(HttpServletRequest request) {
        return electionService.getElections(request);
    }

    @GetMapping("/get-election-by-id")
    public ResponseEntity getElectionById(@RequestParam Integer electionId, HttpServletRequest request) {
        return electionService.getElectionById(electionId,request);
    }

    @PostMapping("/create")
    public ResponseEntity createElection(@Valid @RequestBody Election election, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        return electionService.createElection(election,redirectAttributes,request);
    }

    @PostMapping("/election/set-pollingstations")
    public ResponseEntity addElectionToPollingStations(@RequestParam String name, @RequestBody List<Integer> pollingStationIds, HttpServletRequest request) {
        return electionService.addElectionToPollingStations(name,pollingStationIds, request);
    }

    @GetMapping("/election/pollingstations")
    public ResponseEntity getPollingStations(@RequestParam String name, HttpServletRequest request) {
        return electionService.getPollingStations(name, request);
    }

    @GetMapping("/get-elections-for-user")
    public ResponseEntity getElectionsForUser(HttpServletRequest request) {
        return electionService.getElectionsForUser(request);
    }

    @PostMapping("/election/add-lists")
    public ResponseEntity<String> addLists(@RequestParam String name, @Valid @RequestBody List<Lista> liste, HttpServletRequest request) {
        return electionService.addLists(name,liste,request);
    }


    @GetMapping("/election/lists")
    public ResponseEntity<String> getListsForElections(@RequestParam String name,
                                                       HttpServletRequest request) {
        return electionService.getListsForElections(name, request);
    }

    @PostMapping("/election/add-candidates")
    public ResponseEntity<String> addCandidates(@RequestParam String name, @Valid @RequestBody List<Candidate> candidates, HttpServletRequest request) {
        return electionService.addCandidates(name, candidates, request);
    }

    @GetMapping("/election/candidates")
    public ResponseEntity<String> getCandidates(@RequestParam String name, HttpServletRequest request) {
        return electionService.getCandidates(name, request);
    }

    @GetMapping("/election/get-id")
    public ResponseEntity getElectionIdByName(@RequestParam String electionName, HttpServletRequest request) {
        return electionService.getElectionIdByName(electionName,request);
    }

    @GetMapping("/candidate/get-id")
    public ResponseEntity getCandidateIdByName(@RequestParam String firstName, @RequestParam String lastName, HttpServletRequest request) {
        return electionService.getCandidateIdByName(firstName, lastName,request);
    }

    @GetMapping("/candidate/get-name")
    public ResponseEntity getCandidateNameById(@RequestParam Integer candidateId, HttpServletRequest request) {
        return electionService.getCandidateNameById(candidateId,request);
    }

    @GetMapping("/list/get-name")
    public ResponseEntity getListById(@RequestParam Integer listId, HttpServletRequest request) {
        return electionService.getListById(listId, request);
    }

    @GetMapping("/list/get-id")
    public ResponseEntity getListIdByName(@RequestParam String name,
                                          @RequestParam String electionName,
                                          HttpServletRequest request) {
        return electionService.getListIdByName(name,electionName,request);
    }

    @GetMapping("/get-candidate-by-name")
    public ResponseEntity getCandidateByName(@RequestParam String candidateFirstName, @RequestParam String candidateLastName, HttpServletRequest request) {
        return electionService.getCandidateByName(candidateFirstName,candidateLastName,request);
    }

    @GetMapping("/get-list-by-name")
    public ResponseEntity getListByName(@RequestParam String listaName,
                                        @RequestParam String electionName,
                                        HttpServletRequest request) {
        return electionService.getListByName(listaName, electionName ,request);
    }


}