package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    List<Result> getResultsByElectionNameAndPollingStationName(String electionName, String pollingStationName);
    Optional<Result> getResultByElectionNameAndCandidateFirstNameAndCandidateLastNameAndPollingStationName(String electionName, String candidateFirstName, String candidateLastName, String pollingStationName);
    Optional<Result> getResultByElectionNameAndListNameAndPollingStationName(String electionName, String listName, String pollingStationName);
}