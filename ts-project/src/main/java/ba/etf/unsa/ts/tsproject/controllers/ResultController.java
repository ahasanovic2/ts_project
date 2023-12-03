package ba.etf.unsa.ts.tsproject.controllers;

import ba.etf.unsa.ts.tsproject.services.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/results")
@CrossOrigin(origins = "*")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/full-election")
    public ResponseEntity getFullElectionResults(@RequestParam String electionName, HttpServletRequest request) {
        return resultService.getElectionResultsForPollingStation(electionName, "Total", request);
    }

    @GetMapping("/election/pollingStation")
    public ResponseEntity getElectionResultsForPollingStation(@RequestParam String electionName,
                                                              @RequestParam String pollingStationName,
                                                              HttpServletRequest request) {
        return resultService.getElectionResultsForPollingStation(electionName,pollingStationName,request);
    }

    @GetMapping("/election/candidate")
    public ResponseEntity getElectionResultsForCandidate(@RequestParam String electionName,
                                                         @RequestParam String candidateFirstName,
                                                         @RequestParam String candidateLastName,
                                                         HttpServletRequest request) {
        return resultService.getElectionResultsForCandidate(electionName, candidateFirstName, candidateLastName, "Total", request);
    }

    @GetMapping("/election/pollingStation/candidate")
    public ResponseEntity getElectionResultsForCandidateOnPollingStation(@RequestParam String electionName,
                                                                         @RequestParam String candidateFirstName,
                                                                         @RequestParam String candidateLastName,
                                                                         @RequestParam String pollingStationName,
                                                                         HttpServletRequest request) {
        return resultService.getElectionResultsForCandidate(electionName, candidateFirstName, candidateLastName, pollingStationName, request);
    }

    @GetMapping("/election/list")
    public ResponseEntity getElectionResultsForList(@RequestParam String electionName,
                                                    @RequestParam String listaName,
                                                    HttpServletRequest request) {
        return resultService.getElectionResultsForList(electionName,listaName,"Total",request);
    }

    @GetMapping("/election/pollingStation/list")
    public ResponseEntity getElectionResultsForListForPollingStation(@RequestParam String electionName,
                                                                     @RequestParam String listaName,
                                                                     @RequestParam String pollingStationName,
                                                                     HttpServletRequest request) {
        return resultService.getElectionResultsForList(electionName,listaName,pollingStationName,request);
    }

}