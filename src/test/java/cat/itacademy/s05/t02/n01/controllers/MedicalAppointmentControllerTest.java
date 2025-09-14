package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequestPublic;
import cat.itacademy.s05.t02.n01.services.MedicalAppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@WebFluxTest(controllers = MedicalAppointmentController.class)
@Import(cat.itacademy.s05.t02.n01.config.SecurityConfig.class)
class MedicalAppointmentControllerTest {

    @Autowired
    WebTestClient client;

    @MockBean
    MedicalAppointmentService service;

    @MockBean
    cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository repository;

    @MockBean
    cat.itacademy.s05.t02.n01.mapper.AppointmentMapper mapper;

    @MockBean
    cat.itacademy.s05.t02.n01.repositories.UserRepository users;

    @MockBean
    cat.itacademy.s05.t02.n01.services.JwtService jwtService;

    @MockBean
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockBean
    cat.itacademy.s05.t02.n01.services.UserDetailsService userDetailsService;

    @Test
    void create_request_returns_created() {
        Mockito.when(service.createRequest(Mockito.any(CreateAppointmentRequestPublic.class)))
                .thenReturn(Mono.just(new AppointmentResponse(123L, null, null, null, null, null, null, null, null, null, null, null, null, null, null)));

        String body = "{\"coverageType\":\"PRIVATE\",\"subject\":\"consulta\"}";

        client.post().uri("/api/appointments/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("location", "/api/admin/appointments/123");
    }
}
