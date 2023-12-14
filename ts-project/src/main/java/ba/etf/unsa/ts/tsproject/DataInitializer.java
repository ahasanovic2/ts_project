package ba.etf.unsa.ts.tsproject;

import ba.etf.unsa.ts.tsproject.auth.RegisterRequest;
import ba.etf.unsa.ts.tsproject.entities.Election;
import ba.etf.unsa.ts.tsproject.entities.PollingStation;
import ba.etf.unsa.ts.tsproject.entities.Role;
import ba.etf.unsa.ts.tsproject.entities.User;
import ba.etf.unsa.ts.tsproject.repositories.UserRepository;
import ba.etf.unsa.ts.tsproject.services.AuthenticationService;
import ba.etf.unsa.ts.tsproject.services.ElectionService;
import ba.etf.unsa.ts.tsproject.services.PollingStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthenticationService authenticationService;
    private final PollingStationService pollingStationService;
    private final UserRepository userRepository;
    private final ElectionService electionService;

    @Override
    public void run(String... args) {
        List<User> userList = userRepository.findAll();
        if (userList == null || userList.isEmpty())
            fill();
    }
    private void fill() {
        RegisterRequest admin = createSuperAdmin("Ahmedin", "Hasanovic", "ahasanovic2@etf.unsa.ba", "sifra123");
        RegisterRequest admin2 = createSuperAdmin("Vedad", "Dervisevic", "vdervisevi1@etf.unsa.ba", "sifra456");

        authenticationService.register(admin);
        authenticationService.register(admin2);

        addPollingStations("Izet Sabic", "Donji Hotonj 1", "FederacijaBiH", "Sarajevo", "Vogosca");
        addPollingStations("Mirsad Prnjavorac", "Igmanska 3", "FederacijaBiH", "Sarajevo", "Vogosca");
        addPollingStations("Zahid Barucija", "Igmanska 6", "FederacijaBiH", "Sarajevo", "Vogosca");
        addPollingStations("OS Tuzla", "Tuzlanska 2", "FederacijaBiH", "Tuzlanski", "Tuzla");

        electionService.createElectionInitializer(new Election("Izbori za bosnjackog clana predsjednistva","Ovo je opis za izbore koji ima vise od 20 karaktera", LocalDateTime.now().minusWeeks(5),LocalDateTime.now().plusWeeks(3)));
        electionService.createElectionInitializer(new Election("Izbori za hrvatskog clana predsjednistva","Ovo je opis za izbore koji ima vise od 20 karaktera", LocalDateTime.now().plusWeeks(1),LocalDateTime.now().plusWeeks(2)));
        electionService.createElectionInitializer(new Election("Izbori za srpskog clana predsjednistva","Ovo je opis za izbore koji ima vise od 20 karaktera", LocalDateTime.now().minusWeeks(5),LocalDateTime.now().minusWeeks(4)));
        electionService.createElectionInitializer(new Election("Izbori za nista","Ovo je opis za izbore koji ima vise od 20 karaktera", LocalDateTime.now().minusDays(1),LocalDateTime.now().plusDays(1)));
    }

    private RegisterRequest createSuperAdmin(String firstName, String lastName, String email, String password) {
        RegisterRequest admin = new RegisterRequest();

        admin.setFirstname(firstName);
        admin.setLastname(lastName);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setRole(Role.SUPERADMIN);

        return admin;
    }

    private void addPollingStations(String name, String address, String entitet, String kanton, String opcina) {
        PollingStation pollingStation = new PollingStation(name,address,entitet,kanton,opcina);
        pollingStationService.addPollingStation(pollingStation);
    }
}
