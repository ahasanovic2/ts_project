package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.controllers.ElectionController;
import ba.etf.unsa.ts.tsproject.controllers.UserController;
import ba.etf.unsa.ts.tsproject.entities.Vote;
import ba.etf.unsa.ts.tsproject.entities.VoteMessage;
import ba.etf.unsa.ts.tsproject.repositories.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserController userController;
    private final ElectionController electionController;
    private final ResultService resultService;

    public ResponseEntity<Integer> getUserId() {

        return userController.getId();
    }

    public ResponseEntity getElectionsForUser(HttpServletRequest request) {
        ResponseEntity povrat = electionController.getElectionsForUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(povrat.getBody());
    }

    public ResponseEntity<String> getListsForElection(String name, HttpServletRequest request) {
        System.out.println("Name is " + name);
        ResponseEntity<String> lists = electionController.getListsForElections(name,request);
        return ResponseEntity.status(HttpStatus.OK).body(lists.getBody());
    }

    public ResponseEntity<String> addVoteForCandidate(String electionName, String firstName, String lastName, HttpServletRequest request) {
        ResponseEntity<Integer> userId = getUserId();
        ResponseEntity<Integer> electionId = electionController.getElectionIdByName(electionName,request);
        ResponseEntity<Integer> candidateId = electionController.getCandidateIdByName(firstName,lastName,request);
        Vote vote = new Vote();
        vote.setVoterId(userId.getBody());
        vote.setCandidateId(candidateId.getBody());
        vote.setElectionId(electionId.getBody());
        vote.setTimestamp(LocalDateTime.now().toString());
        voteRepository.save(vote);

        VoteMessage voteMessage = new VoteMessage();
        voteMessage.setId(vote.getId());
        voteMessage.setCandidateId(vote.getCandidateId());
        voteMessage.setTimestamp(vote.getTimestamp());
        voteMessage.setVoterId(vote.getVoterId());
        voteMessage.setElectionId(vote.getElectionId());
        voteMessage.setListaId(null);

        resultService.consumeMessageFromQueue(voteMessage,request);


        return ResponseEntity.status(HttpStatus.OK).body("Successfully added vote to candidate");
    }

    public ResponseEntity<String> addVoteForList(String electionName, String name, HttpServletRequest request) {
        ResponseEntity<Integer> userId = getUserId();
        ResponseEntity<Integer> electionId = electionController.getElectionIdByName(electionName,request);
        ResponseEntity<Integer> listId = electionController.getListIdByName(name,electionName,request);
        Vote vote = new Vote();
        vote.setVoterId(userId.getBody());
        vote.setElectionId(electionId.getBody());
        vote.setListaId(listId.getBody());
        vote.setTimestamp(LocalDateTime.now().toString());
        voteRepository.save(vote);

        VoteMessage voteMessage = new VoteMessage();
        voteMessage.setId(vote.getId());
        voteMessage.setListaId(vote.getListaId());
        voteMessage.setTimestamp(vote.getTimestamp());
        voteMessage.setVoterId(vote.getVoterId());
        voteMessage.setElectionId(vote.getElectionId());
        voteMessage.setCandidateId(null);

        resultService.consumeMessageFromQueue(voteMessage,request);


        return ResponseEntity.status(HttpStatus.OK).body("Successfully added vote to list");
    }

    public ResponseEntity<String> getVoteByElection(Integer electionId, Integer userId) {
        Optional<Vote> optionalVote = voteRepository.getVoteByElectionIdAndAndVoterId(electionId,userId);
        if (optionalVote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("{\"hasVote\":false}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"hasVote\":true}");
    }

    public ResponseEntity<String> getAllVotes() {
        List<Vote> votes = voteRepository.findAll();
        return getVotesInString(votes);
    }

    public ResponseEntity getAllVotesForElection(Integer electionId) {
        List<Vote> votes = voteRepository.findAllByElectionId(electionId);
        return getVotesInString(votes);
    }

    private ResponseEntity getVotesInString(List<Vote> votes) {
        ObjectMapper mapper = new ObjectMapper();
        String votesJsonString;
        try {
            votesJsonString = mapper.writeValueAsString(votes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to convert votes to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(votesJsonString, HttpStatus.OK);
    }
}