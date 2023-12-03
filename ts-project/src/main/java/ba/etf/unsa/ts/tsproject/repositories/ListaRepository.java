package ba.etf.unsa.ts.tsproject.repositories;

import ba.etf.unsa.ts.tsproject.entities.Election;
import ba.etf.unsa.ts.tsproject.entities.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaRepository extends JpaRepository<Lista, Integer> {

    List<Lista> findAllByElectionId(Long electionId);

    Optional<Lista> getListaByName(String listaName);

    List<Lista> getListasByElectionName(String name);

    Optional<Lista> getListaByNameAndElection(String name, Election election);
}