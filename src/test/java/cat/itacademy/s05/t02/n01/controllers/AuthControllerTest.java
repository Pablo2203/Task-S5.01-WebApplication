
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
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtService jwtService;

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
        Mockito.when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        Mockito.when(jwtService.generateToken(Mockito.eq("admin"), Mockito.anyCollection())).thenReturn("token123");

        webTestClient.post().uri("/auth/login")
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
}
