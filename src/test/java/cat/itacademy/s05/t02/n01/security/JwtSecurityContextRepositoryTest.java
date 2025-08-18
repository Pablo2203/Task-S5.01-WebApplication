package cat.itacademy.s05.t02.n01.security;

import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "security.jwt.secret=12345678901234567890123456789012",
                "security.jwt.expiration-minutes=60"
        })
@AutoConfigureWebTestClient
class JwtSecurityContextRepositoryTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private VirtualAssistantRepository assistantRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        when(assistantRepository.findAll()).thenReturn(Flux.just(new VirtualAssistant()));
        when(userDetailsService.findByUsername("user")).thenReturn(
                Mono.just(User.withUsername("user").password("pass").roles("USER").build())
        );
    }

    @Test
    void requestWithoutTokenReturnsUnauthorized() {
        webTestClient.get().uri("/api/virtual-assistant")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithValidTokenIsAuthenticated() {
        String token = jwtService.generateToken("user", List.of("USER"));
        webTestClient.get().uri("/api/virtual-assistant")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }
}