
package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AuthController.class)
@Import(cat.itacademy.s05.t02.n01.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtService jwtService;

    @MockBean
    cat.itacademy.s05.t02.n01.services.UserDetailsService userDetailsService;

    @MockBean
    cat.itacademy.s05.t02.n01.services.MailService mailService;

    @Test
    void login_ok_with_username() {
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setEmail("admin@example.com");
        u.setPasswordHash("hash");
        u.setRole("ADMIN");
        u.setEnabled(true);

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(Mono.just(u));
        Mockito.when(userRepository.findByEmail("admin")).thenReturn(Mono.empty());
        Mockito.when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        Mockito.when(jwtService.generateToken(Mockito.eq("admin"), Mockito.anyCollection())).thenReturn("token123");

        webTestClient.mutateWith(csrf()).post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"admin\",\"password\":\"pass\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(resp -> {
                    assert resp.token() != null;
                    assert resp.roles().contains("ADMIN");
                });
    }

    @Test
    void login_ok_with_email() {
        User u = new User();
        u.setId(2L);
        u.setUsername("john");
        u.setEmail("john@example.com");
        u.setPasswordHash("hash2");
        u.setRole("PATIENT");
        u.setEnabled(true);

        Mockito.when(userRepository.findByUsername("john@example.com")).thenReturn(Mono.empty());
        Mockito.when(userRepository.findByEmail("john@example.com")).thenReturn(Mono.just(u));
        Mockito.when(passwordEncoder.matches("pass", "hash2")).thenReturn(true);
        Mockito.when(jwtService.generateToken(Mockito.eq("john"), Mockito.anyCollection())).thenReturn("tok2");

        webTestClient.mutateWith(csrf()).post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"username\":\"john@example.com\",\"password\":\"pass\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(resp -> {
                    assert resp.token() != null;
                    assert resp.roles().contains("PATIENT");
                });
    }

    @Test
    void register_ok_creates_user() {
        Mockito.when(userRepository.findByUsername("newuser")).thenReturn(Mono.empty());
        Mockito.when(userRepository.findByEmail("new@user.com")).thenReturn(Mono.empty());

        User saved = new User();
        saved.setId(100L);
        saved.setUsername("newuser");
        saved.setEmail("new@user.com");
        saved.setPasswordHash("enc");
        saved.setRole("PATIENT");
        saved.setEnabled(true);

        Mockito.when(passwordEncoder.encode("secret123")).thenReturn("enc");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(Mono.just(saved));

        String body = "{\"username\":\"newuser\",\"email\":\"new@user.com\",\"password\":\"secret123\",\"roleWanted\":\"PATIENT\"}";

        webTestClient.mutateWith(csrf()).post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.ok").isEqualTo(true);
    }
}
