package ba.etf.unsa.ts.tsproject.authconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static ba.etf.unsa.ts.tsproject.entities.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/authentication/**").permitAll()
                .requestMatchers(POST, "/pollingStations/create").hasAnyRole(ADMIN.name(), SUPERADMIN.name())
                .requestMatchers(GET, "/pollingStations").hasAnyRole(ADMIN.name(), USER.name(), SUPERADMIN.name())
                .requestMatchers(GET, "/pollingStations/user").hasAnyRole(ADMIN.name(), USER.name(), SUPERADMIN.name())
                .requestMatchers(GET, "/pollingStations/user/get-by-id").hasAnyRole(ADMIN.name(), USER.name(), SUPERADMIN.name())
                .requestMatchers(GET, "/pollingStations/get-by-name").hasAnyRole(ADMIN.name(), USER.name(), SUPERADMIN.name())
                .requestMatchers("/pollingStations/**").denyAll()
                .requestMatchers(GET, "/users").hasAnyRole(ADMIN.name(), SUPERADMIN.name())
                .requestMatchers(GET, "/users/admins").hasRole(SUPERADMIN.name())
                .requestMatchers(GET, "/users/users").hasAnyRole(ADMIN.name(), SUPERADMIN.name())
                .requestMatchers(POST, "/users/create-admin").hasRole(SUPERADMIN.name())
                .requestMatchers(PUT, "/users/change-password").hasAnyRole(SUPERADMIN.name(), ADMIN.name(), USER.name())
                .requestMatchers(DELETE, "/users/delete-admin").hasRole(SUPERADMIN.name())
                .requestMatchers(POST, "/users/{userId}/pollingStation/{pollingStationId}").hasAnyRole(USER.name(), SUPERADMIN.name())
                .requestMatchers(POST, "/users/{userId}/pollingStation").hasAnyRole(ADMIN.name(), SUPERADMIN.name())
                .requestMatchers(POST, "/users/pollingStation/{pollingStationId}").hasAnyRole(USER.name(), SUPERADMIN.name())
                .requestMatchers(POST, "/users/pollingStation").hasAnyRole(USER.name(), SUPERADMIN.name())
                .requestMatchers(GET,"/users/id").hasAnyRole(USER.name(), ADMIN.name(), SUPERADMIN.name())
                .requestMatchers("/users/**").denyAll()
                .requestMatchers(GET,"/voting/elections").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/voting/election/get-lists").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/voting/election/list/get-candidates").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(POST,"/voting/vote-for-candidate").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(POST,"/voting/vote-for-list").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/voting/get-vote-by-election").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/voting/votes").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/voting/votes-for-election").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers("/voting/**").denyAll()
                .requestMatchers(GET, "/elections").hasAnyRole("USER", "ADMIN", SUPERADMIN.name())
                .requestMatchers(POST, "/elections/create").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(POST, "/elections/election/set-pollingstations").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(GET, "/elections/election/pollingstations").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(POST, "/elections/election/add-lists").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(GET, "/elections/election/lists").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET, "/elections/election/candidates").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(POST, "/elections/election/add-candidates").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/get-elections-for-user").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/election/get-lists").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/election/candidates").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/election/get-id").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/candidate/get-id").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/candidate/get-name").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/list/get-id").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/list/get-name").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/get-candidate-by-name").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/get-list-by-name").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/elections/get-election-by-id").hasAnyRole("USER","ADMIN", SUPERADMIN.name())
                .requestMatchers("/elections/**").denyAll()
                .requestMatchers(GET,"/results").hasAnyRole("ADMIN", SUPERADMIN.name())
                .requestMatchers(GET,"/results/full-election").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/pollingStation").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/candidate").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/list").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/pollingStation/candidate").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/pollingStation/list").hasAnyRole("ADMIN","USER", SUPERADMIN.name())
                .requestMatchers(GET,"/results/election/{election_id}/pollingStation/{pollingStationId}/candidate/{candidateId}/show").hasAnyRole("ADMIN", "USER", SUPERADMIN.name())
                .requestMatchers("/results/**").denyAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/authentication/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}