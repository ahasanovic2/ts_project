package ba.etf.unsa.ts.tsproject.services;

import ba.etf.unsa.ts.tsproject.controllers.PollingStationController;
import ba.etf.unsa.ts.tsproject.controllers.UserController;
import ba.etf.unsa.ts.tsproject.entities.*;
import ba.etf.unsa.ts.tsproject.exception.ErrorDetails;
import ba.etf.unsa.ts.tsproject.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ListaRepository listaRepository;
    private final CandidateRepository candidateRepository;
    private final PollingStationRepository pollingStationRepository;
    private final UserController userController;
    private final PollingStationController pollingStationController;
    private final PollingStationService pollingStationService;
    private final VoteRepository voteRepository;

    public ResponseEntity<String> getVoteByElection(Integer electionId, Integer userId, HttpServletRequest request) {
        Optional<Vote> optionalVote = voteRepository.getVoteByElectionIdAndAndVoterId(electionId,userId);
        if (optionalVote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("{\"hasVote\":false}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"hasVote\":true}");
    }

    private ResponseEntity<String> checkElectionExists(String name) {
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"electionId","ELection ID not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return null;
    }


    public List<PollingStation> deserializePollingStations(String pollingStationsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<PollingStation> pollingStations = null;
        try {
            pollingStations = objectMapper.readValue(pollingStationsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return pollingStations;
    }


    public ResponseEntity getElections() {
        List<Election> elections = electionRepository.findAll();
        String json;
        try {
            StringBuilder sb = new StringBuilder("[");
            for (Election election : elections) {
                sb.append(election.toString()).append(",");
            }
            if (elections.size() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            json = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error serializing elections to JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(LocalDateTime.now(),"election","Error serializing to JSON"));
        }
        System.out.println("Serialized JSON: " + json);
        return ResponseEntity.status(HttpStatus.OK).body(json);
    }

    public void createElectionInitializer(Election election) {
        electionRepository.save(election);
    }

    public ResponseEntity createElection(Election election, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addAttribute("electionId", election.getId());
        List<PollingStation> pollingStations = new ArrayList<>();
        for (PollingStation pollingStation: election.getPollingStations()) {
            Integer id = pollingStation.getId();
            Optional<PollingStation> optionalPollingStation = pollingStationRepository.findById(id);
            if (optionalPollingStation.isPresent()) {
                pollingStations.add(optionalPollingStation.get());
            }
            else {
                ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"pollingStationId","Polling Station ID not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
            }
        }
        for (PollingStation pollingStation: pollingStations) {
            pollingStation.getElections().add(election);
            pollingStationRepository.save(pollingStation);
        }
        if (election.getStartTime().isAfter(LocalDateTime.now()))
            election.setStatus("NotStarted");
        else if (LocalDateTime.now().isAfter(election.getEndTime()))
            election.setStatus("Finished");
        else
            election.setStatus("Active");
        electionRepository.save(election);
        Map<String, Object> response = new HashMap<>();
        response.put("id", election.getId());
        response.put("name", election.getName());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> addLists(String name, List<Lista> liste, HttpServletRequest request) {
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"electionId","ELection ID not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        Election election = optionalElection.get();
        for (Lista lista : liste) {
            lista.setElection(election);
            listaRepository.save(lista);
        }
        return ResponseEntity.ok("Lists added successfully to election " + election.getId());
    }

    public ResponseEntity<String> getListsForElections(String name,HttpServletRequest request) {
        name = URLDecoder.decode(name,StandardCharsets.UTF_8);
        ResponseEntity<Integer> userId = getUserId(request);
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","Election namenot found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        List<Lista> lists = listaRepository.getListasByElectionName(name);
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(lists);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error while parsing to string");
        }
        return ResponseEntity.ok(json);
    }

    public ResponseEntity<String> addCandidates(String name, List<Candidate> candidates, HttpServletRequest request) {
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"electionId","ELection ID not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        Election election = optionalElection.get();
        for (Candidate candidate: candidates) {
            candidate.setElection(election);
            candidateRepository.save(candidate);
            election.addCandidate(candidate);
        }
        electionRepository.save(election);
        return ResponseEntity.ok("Candidates added successfully to election " + election.getId());
    }

    public ResponseEntity<String> getCandidates(String name, HttpServletRequest request) {
        ResponseEntity<Integer> userId = getUserId(request);
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"electionId","ELection ID not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }

        List<Candidate> candidates = candidateRepository.getCandidatesByElectionName(name);
        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(candidates);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error while parsing to string");
        }
        return ResponseEntity.ok(json);
    }

    public ResponseEntity<String> getPollingStations(String name, HttpServletRequest request) {
        ResponseEntity<String> responseEntity = checkElectionExists(name);
        if (responseEntity != null) {
            return responseEntity;
        }
        ResponseEntity response = pollingStationController.getPollingStations();
        return response;
    }

    public ResponseEntity addElectionToPollingStations(String name, List<Integer> pollingStationIds, HttpServletRequest request) {
        Optional<Election> optionalElection = electionRepository.getElectionByName(name);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"electionId","Election ID not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        Election election = optionalElection.get();
        List<PollingStation> pollingStations = pollingStationRepository.getPollingStationsByIdIsIn(pollingStationIds);
        if (pollingStations.isEmpty() || pollingStations == null) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"pollingstationId","No polling stations");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        for (PollingStation pollingStation: pollingStations) {
            pollingStation.addElections(election);
            election.addPollingStation(pollingStation);
            pollingStationRepository.save(pollingStation);
        }
        electionRepository.save(election);
        return ResponseEntity.status(HttpStatus.OK).body(pollingStations);
    }

    public ResponseEntity<Integer> getUserId(HttpServletRequest request) {
        return userController.getId();
    }

    public String getElectionsForUser(HttpServletRequest request) {
        PollingStation pollingStation = (PollingStation) pollingStationController.getPollingStationForUser().getBody();
        List<Election> elections = electionRepository.findElectionsByPollingStationName(pollingStation.getName());

        List<Election> electionsToRemove = new ArrayList<>();

        for (Election election: elections) {
            ResponseEntity<String> povrat = getVoteByElection(Integer.valueOf(election.getId().intValue()), getUserId(request).getBody(), request);
            ObjectMapper mapper = new ObjectMapper();
            Boolean hasVote = null;

            try {
                VoteStatus voteStatus = mapper.readValue(povrat.getBody(), VoteStatus.class);
                if (voteStatus != null) {
                    hasVote = voteStatus.getHasVote();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (hasVote != null && hasVote.booleanValue()) {
                electionsToRemove.add(election);
            }
        }

        elections.removeAll(electionsToRemove);

        List<String> electionStrings = elections.stream().map(Election::toString).collect(Collectors.toList());
        String jsonArray = String.join(", ", electionStrings);
        jsonArray = "[" + jsonArray + "]";

        return jsonArray;
    }


    public ResponseEntity getElectionIdByName(String electionName, HttpServletRequest request) {
        electionName = URLDecoder.decode(electionName,StandardCharsets.UTF_8);
        Optional<Election> optionalElection = electionRepository.getElectionByName(electionName);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","No election by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        /*if (!optionalElection.get().getStatus().equals("Finished")) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"status","Election is not finished");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }*/
        return ResponseEntity.status(HttpStatus.OK).body(optionalElection.get().getId());
    }

    public ResponseEntity getCandidateIdByName(String firstName, String lastName, HttpServletRequest request) {
        ResponseEntity<Integer> userId = getUserId(request);
        firstName = URLDecoder.decode(firstName,StandardCharsets.UTF_8);
        lastName = URLDecoder.decode(lastName,StandardCharsets.UTF_8);
        Optional<Candidate> optionalCandidate = candidateRepository.getCandidateByFirstNameAndLastName(firstName,lastName);
        if (optionalCandidate.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","No candidate by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalCandidate.get().getId());
    }

    public ResponseEntity getListIdByName(String name, String electionName, HttpServletRequest request) {
        electionName = URLDecoder.decode(electionName,StandardCharsets.UTF_8);
        ResponseEntity<Integer> userId = getUserId(request);
        name = URLDecoder.decode(name,StandardCharsets.UTF_8);
        Optional<Election> optionalElection = electionRepository.getElectionByName(electionName);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","No election by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        Optional<Lista> optionalLista = listaRepository.getListaByNameAndElection(name, optionalElection.get());
        if (optionalLista.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","No list by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalLista.get().getId());

    }

    public ResponseEntity getCandidateNameById(Integer candidateId, HttpServletRequest request) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);
        Integer userId = getUserId(request).getBody();
        if (optionalCandidate.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"id","No candidate by that id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalCandidate.get());
    }

    public ResponseEntity getListById(Integer listId, HttpServletRequest request) {
        Optional<Lista> optionalLista = listaRepository.findById(listId);
        Integer userId = getUserId(request).getBody();
        if (optionalLista.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"id","No list by that id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalLista.get());
    }

    public ResponseEntity getCandidateByName(String candidateFirstName, String candidateLastName, HttpServletRequest request) {
        Optional<Candidate> optionalCandidate = candidateRepository.getCandidateByFirstNameAndLastName(candidateFirstName,candidateLastName);
        Integer userId = getUserId(request).getBody();
        if (optionalCandidate.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "name", "No candidate by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(optionalCandidate.get());
    }

    public ResponseEntity getListByName(String listaName, String electionName, HttpServletRequest request) {
        electionName = URLDecoder.decode(electionName,StandardCharsets.UTF_8);
        listaName = URLDecoder.decode(listaName,StandardCharsets.UTF_8);
        Integer userId = getUserId(request).getBody();
        Optional<Election> optionalElection = electionRepository.getElectionByName(electionName);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),"name","No election by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }

        Optional<Lista> optionalLista = listaRepository.getListaByNameAndElection(listaName, optionalElection.get());
        if (optionalLista.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "name", "No list by that name");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(optionalLista.get());
    }

    public ResponseEntity getElectionById(Integer electionId, HttpServletRequest request) {
        Optional<Election> optionalElection = electionRepository.findById(electionId);
        if (optionalElection.isEmpty()) {
            ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "ID", "No election by that ID");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails.toString());
        }
        return ResponseEntity.ok(optionalElection.get());
    }
}