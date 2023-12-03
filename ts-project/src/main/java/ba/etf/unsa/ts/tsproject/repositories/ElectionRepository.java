package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Integer> {

    @Query("SELECT e FROM Election e JOIN e.pollingStations ps WHERE ps.name = :name")
    List<Election> findElectionsByPollingStationName(@Param("name") String name);

    Optional<Election> getElectionByName(String name);
}