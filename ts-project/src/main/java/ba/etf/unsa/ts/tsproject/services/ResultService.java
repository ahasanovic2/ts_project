package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.controllers.ElectionController;
import ba.etf.unsa.ts.tsproject.controllers.PollingStationController;
import ba.etf.unsa.ts.tsproject.entities.*;
import ba.etf.unsa.ts.tsproject.exception.ErrorDetails;
import ba.etf.unsa.ts.tsproject.repositories.ResultRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final PollingStationController pollingStationController;
    private final ElectionController electionController;

    public ResponseEntity getElectionResultsForPollingStation(String electionName, String pollingStationName, HttpServletRequest request) {
        List<Result> results = resultRepository.getResultsByElectionNameAndPollingStationName(electionName,pollingStationName);
        if (results.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Get full election results", "No results found for that election");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(results);
    }

    public ResponseEntity getElectionResultsForCandidate(String electionName, String candidateFirstName, String candidateLastName, String pollingStationName, HttpServletRequest request) {
        Optional<Result> optionalResult = resultRepository.getResultByElectionNameAndCandidateFirstNameAndCandidateLastNameAndPollingStationName(
                electionName, candidateFirstName, candidateLastName, pollingStationName
        );
        if (optionalResult.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Get full election results", "No results found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(optionalResult.get());
    }

    public ResponseEntity getElectionResultsForList(String electionName, String listaName, String pollingStationName, HttpServletRequest request) {
        Optional<Result> optionalResult = resultRepository.getResultByElectionNameAndListNameAndPollingStationName(
                electionName, listaName, pollingStationName
        );
        if (optionalResult.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Get full election results", "No results found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(optionalResult.get());
    }

    private PollingStation getPollingStation(Integer userId, HttpServletRequest request) {
        ResponseEntity<PollingStation> responseEntity = pollingStationController.getPollingStationByUserId(userId);
        return responseEntity.getBody();
    }

    private Candidate getCandidate(Integer candidateId, HttpServletRequest request) {
        ResponseEntity<Candidate> responseEntity = electionController.getCandidateNameById(candidateId,request);
        return responseEntity.getBody();

    }

    private Lista getLista(Integer listaId, HttpServletRequest request) {
        ResponseEntity<Lista> responseEntity = electionController.getListById(listaId,request);
        Lista lista = responseEntity.getBody();
        return  lista;
    }



    private Election getElection(Integer electionId, HttpServletRequest request) {
        ResponseEntity<Election> responseEntity = electionController.getElectionById(electionId,request);
        Election election = responseEntity.getBody();
        return election;
    }

    public void consumeMessageFromQueue(VoteMessage voteMessage, HttpServletRequest request) {
        PollingStation pollingStation = getPollingStation(voteMessage.getVoterId(), request);
        Election election = getElection(voteMessage.getElectionId(), request);
        if (voteMessage.getCandidateId() != null) {
            Candidate candidate = getCandidate(voteMessage.getCandidateId(), request);
            Optional<Result> optionalResult = resultRepository.getResultByElectionNameAndCandidateFirstNameAndCandidateLastNameAndPollingStationName(
                    election.getName(), candidate.getFirstName(), candidate.getLastName(), pollingStation.getName()
            );
            if (optionalResult.isEmpty()) {
                Result result = new Result();
                result.setElectionName(election.getName());
                result.setCandidateLastName(candidate.getLastName());
                result.setCandidateFirstName(candidate.getFirstName());
                result.setVoteCount(1);
                result.setPollingStationName(pollingStation.getName());
                resultRepository.save(result);
            }
            else {
                Result result = optionalResult.get();
                result.setVoteCount(result.getVoteCount() + 1);
                resultRepository.save(result);
            }

            optionalResult = resultRepository.getResultByElectionNameAndCandidateFirstNameAndCandidateLastNameAndPollingStationName(
                    election.getName(), candidate.getFirstName(), candidate.getLastName(), "Total"
            );
            if (optionalResult.isEmpty()) {
                Result result1 = new Result();
                result1.setElectionName(election.getName());
                result1.setCandidateLastName(candidate.getLastName());
                result1.setCandidateFirstName(candidate.getFirstName());
                result1.setVoteCount(1);
                result1.setPollingStationName("Total");
                resultRepository.save(result1);
            }
            else {
                Result result1 = optionalResult.get();
                result1.setVoteCount(result1.getVoteCount() + 1);
                resultRepository.save(result1);
            }
        }
        else if (voteMessage.getListaId() != null) {
            Lista lista = getLista(voteMessage.getListaId(), request);
            Optional<Result> optionalResult = resultRepository.getResultByElectionNameAndListNameAndPollingStationName(
                    election.getName(), lista.getName(), pollingStation.getName()
            );
            if (optionalResult.isEmpty()) {
                Result result = new Result();
                result.setElectionName(election.getName());
                result.setListName(lista.getName());
                result.setVoteCount(1);
                result.setPollingStationName(pollingStation.getName());
                resultRepository.save(result);
            } else {
                Result result = optionalResult.get();
                result.setVoteCount(result.getVoteCount() + 1);
                resultRepository.save(result);
            }
            optionalResult = resultRepository.getResultByElectionNameAndListNameAndPollingStationName(
                    election.getName(), lista.getName(), "Total"
            );
            if (optionalResult.isEmpty()) {
                Result result1 = new Result();
                result1.setElectionName(election.getName());
                result1.setListName(lista.getName());
                result1.setVoteCount(1);
                result1.setPollingStationName("Total");
                resultRepository.save(result1);
            }
            else {
                Result result1 = optionalResult.get();
                result1.setVoteCount(result1.getVoteCount() + 1);
                resultRepository.save(result1);
            }
        }
    }
}