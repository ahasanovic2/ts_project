package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    List<Vote> findAllByElectionId(Integer electionId);
    Optional<Vote> getVoteByElectionIdAndAndVoterId(Integer electionId, Integer voterId);
}