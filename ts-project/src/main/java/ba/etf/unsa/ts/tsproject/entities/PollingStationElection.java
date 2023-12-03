package ba.etf.unsa.ts.tsproject.entities;

import jakarta.persistence.*;

public class PollingStationElection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polling_station_id")
    private PollingStation pollingStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PollingStation getPollingStation() {
        return pollingStation;
    }

    public void setPollingStation(PollingStation pollingStation) {
        this.pollingStation = pollingStation;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }
}