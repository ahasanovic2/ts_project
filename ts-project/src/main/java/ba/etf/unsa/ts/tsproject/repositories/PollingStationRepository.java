package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.PollingStation;
import ba.etf.unsa.ts.tsproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollingStationRepository extends JpaRepository<PollingStation, Integer> {
    Optional<PollingStation> findByName(String name);
    Optional<PollingStation> getPollingStationByUsers(User user);
    List<PollingStation> getPollingStationsByIdIsIn(List<Integer> ids);
}