package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.ScheduleAppointmentRequest;
import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.mapper.AppointmentMapper;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

class MedicalAppointmentServiceTest {

    @Test
    void schedule_conflict_when_overlapping() {
        MedicalAppointmentRepository repo = Mockito.mock(MedicalAppointmentRepository.class);
        AppointmentMapper mapper = Mockito.mock(AppointmentMapper.class);
        UserRepository users = Mockito.mock(UserRepository.class);
        MedicalAppointmentService service = new MedicalAppointmentService(repo, mapper, users);

        MedicalAppointment existing = MedicalAppointment.builder()
                .id(10L)
                .status(AppointmentStatus.REQUESTED)
                .build();

        Mockito.when(repo.findById(10L)).thenReturn(Mono.just(existing));
        Mockito.when(repo.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(true));

        ScheduleAppointmentRequest req = new ScheduleAppointmentRequest(1L, LocalDateTime.now(), null);

        StepVerifier.create(service.scheduleAppointment(10L, req))
                .expectErrorSatisfies(err -> {
                    assert err instanceof ResponseStatusException;
                    ResponseStatusException ex = (ResponseStatusException) err;
                    assert ex.getStatusCode().value() == 409;
                })
                .verify();
    }

    @Test
    void schedule_success_when_no_overlap() {
        MedicalAppointmentRepository repo = Mockito.mock(MedicalAppointmentRepository.class);
        AppointmentMapper mapper = Mockito.mock(AppointmentMapper.class);
        UserRepository users = Mockito.mock(UserRepository.class);
        MedicalAppointmentService service = new MedicalAppointmentService(repo, mapper, users);

        MedicalAppointment existing = MedicalAppointment.builder()
                .id(11L)
                .status(AppointmentStatus.REQUESTED)
                .build();

        Mockito.when(repo.findById(11L)).thenReturn(Mono.just(existing));
        Mockito.when(repo.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(Mockito.eq(5L), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(false));
        Mockito.when(repo.save(Mockito.any(MedicalAppointment.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // map to response
        Mockito.when(mapper.toResponse(Mockito.any(MedicalAppointment.class))).thenAnswer(inv -> {
            MedicalAppointment a = inv.getArgument(0);
            return new AppointmentResponse(a.getId(), a.getProfessionalId(), null, a.getStartsAt(), a.getEndsAt(), a.getStatus(), null, null, null, null, null, null, null, null, null);
        });

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        ScheduleAppointmentRequest req = new ScheduleAppointmentRequest(5L, start, null);

        StepVerifier.create(service.scheduleAppointment(11L, req))
                .assertNext(resp -> {
                    assert resp.professionalId().equals(5L);
                    assert resp.startsAt().equals(start);
                    assert resp.status() == AppointmentStatus.SCHEDULED;
                })
                .verifyComplete();
    }
}
