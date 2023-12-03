package ba.etf.unsa.ts.tsproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Election {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "This field cannot be null")
    @NotBlank(message = "This field cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9]+(\\s+[a-zA-Z0-9]+)*$", message = "You can only enter alphabet characters and numbers.")
    @Column(unique = true)
    private String name;

    @NotNull(message = "This field cannot be null")
    @Size(min = 20, message = "This field must contain at least 20 characters.")
    private String description;

    @NotNull(message = "Start time must not be null")
    private LocalDateTime startTime;

    @NotNull(message = "This field cannot be null")
    private LocalDateTime endTime;

    @Pattern(regexp = "^(Active|Finished|NotStarted)$", message = "This field can only be Active, Finished and NotStarted")
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "election")
    private List<Lista> list = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "election")
    private List<Candidate> candidates = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "elections")
    @JsonIgnore
    private List<PollingStation> pollingStations = new ArrayList<>();

    @AssertTrue(message = "End time must be after start time")
    public boolean isEndTime() {
        if (endTime == null || startTime == null) {
            return true;
        }
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
        return endTime.isAfter(startTime);
    }
    public Election() {

    }

    public Election(String name, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime))
            throw new IllegalArgumentException("Not allowed startTime after endTime");
        if (startTime.isBefore(LocalDateTime.now()) || startTime.isEqual(LocalDateTime.now())) {
            if (endTime.isAfter(LocalDateTime.now()))
                this.status = "Active";
            else if (endTime.isBefore(LocalDateTime.now()))
                this.status = "Finished";
        }
        else if (startTime.isAfter(LocalDateTime.now()))
            this.status = "NotStarted";

    }

    public Election(String name, String description, LocalDateTime startTime, LocalDateTime endTime, String status, List<Lista> list, List<PollingStation> pollingStations) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.list = list;
        this.pollingStations = pollingStations;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime start_time) {
        this.startTime = start_time;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime end_time) {
        this.endTime = end_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Lista> getList() {
        return list;
    }

    public void setList(List<Lista> list) {
        this.list = list;
    }

    public void addLista(Lista list) {
        this.list.add(list);
    }

    public List<PollingStation> getPollingStations() {
        return pollingStations;
    }

    public void addPollingStation(PollingStation pollingStation) {
        this.pollingStations.add(pollingStation);
    }

    public void setPollingStations(List<PollingStation> pollingStations) {
        this.pollingStations = pollingStations;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setEndTime(String endTime) {
        this.endTime = LocalDateTime.parse(endTime,DateTimeFormatter.ISO_DATE_TIME);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public void addCandidate(Candidate candidate) {
        this.candidates.add(candidate);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id +
                "\", \"name\":\"" + name + '\"' +
                ", \"startTime\":\"" + startTime.format(DATE_TIME_FORMATTER) +
                "\", \"endTime\":\"" + endTime.format(DATE_TIME_FORMATTER) +
                "\", \"status\":\"" + status + '\"' +
                ", \"description\":\"" + description + '\"' +
                '}';
    }
}