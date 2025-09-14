package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.MailService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Date;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = AuthController.class)
@Import(cat.itacademy.s05.t02.n01.config.SecurityConfig.class)
class AuthExtraControllerTest {

    @Autowired
    WebTestClient client;

    @MockBean
    UserRepository users;

    @MockBean
    PasswordEncoder passwordEncoder;

@MockBean
JwtService jwtService;

@MockBean
MailService mailService;

@MockBean
cat.itacademy.s05.t02.n01.services.UserDetailsService userDetailsService;

    @Test
    void forgot_password_ok() {
        User u = new User(); u.setUsername("john"); u.setEmail("john@example.com"); u.setRole("PATIENT");
        Mockito.when(users.findByEmail("john@example.com")).thenReturn(Mono.just(u));
        Mockito.when(jwtService.generateTokenWithPurpose(Mockito.eq("john"), Mockito.anyCollection(), Mockito.eq("reset"), Mockito.anyLong()))
                .thenReturn("tok");

        client.post().uri("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"john@example.com\"}")
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(mailService, Mockito.times(1))
                .send(Mockito.eq("john@example.com"), Mockito.anyString(), Mockito.contains("reset-password"));
    }

    @Test
    void reset_password_ok() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("john")
                .claim("purpose", "reset")
                .expirationTime(new Date(System.currentTimeMillis()+60000))
                .build();
        Mockito.when(jwtService.validate("tok")).thenReturn(claims);

        User u = new User(); u.setUsername("john"); u.setPasswordHash("old");
        Mockito.when(users.findByUsername("john")).thenReturn(Mono.just(u));
        Mockito.when(passwordEncoder.encode("newpass")).thenReturn("enc");
        Mockito.when(users.save(ArgumentMatchers.any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        client.post().uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"token\":\"tok\",\"password\":\"newpass\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void confirm_email_redirects() throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject("john@example.com")
                .claim("purpose", "confirm")
                .expirationTime(new Date(System.currentTimeMillis()+60000))
                .build();
        Mockito.when(jwtService.validate("tok")).thenReturn(claims);

        User u = new User(); u.setEmail("john@example.com");
        Mockito.when(users.findByEmail("john@example.com")).thenReturn(Mono.just(u));
        Mockito.when(users.save(ArgumentMatchers.any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        client.get().uri("/auth/confirm-email?token=tok")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/confirmar-email\\?ok=1$");
    }
}
