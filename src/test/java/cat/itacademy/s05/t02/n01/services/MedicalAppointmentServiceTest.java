/*
package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.PatientProfile;
import cat.itacademy.s05.t02.n01.enums.DocumentType;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import cat.itacademy.s05.t02.n01.mapper.AppointmentMapperTest;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalAppointmentServiceTest {

    @Mock
    private MedicalAppointmentRepository repository;

    @Mock
    private PatientProfileService patientProfileService;

    private AppointmentMapperTest mapper = Mappers.getMapper(AppointmentMapperTest.class);

    @InjectMocks
    private MedicalAppointmentService service;

    @BeforeEach
    void setup() {
        service = new MedicalAppointmentService(repository, mapper, patientProfileService);
    }

    private CreateAppointmentRequest baseRequest(LocalDateTime start) {
        return new CreateAppointmentRequest(
                99L,
                Specialty.PSICOLOGIA,
                start,
                "Juan",
                "Perez",
                DocumentType.DNI,
                "12345678",
                "juan@example.com",
                "+123456789",
                null,
                null,
                null,
                null,
                null,
                true
        );
    }

    @Test
    void createAppointmentCalculatesEndsAt() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        CreateAppointmentRequest request = baseRequest(start);

        when(repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(eq(99L), any(), any()))
                .thenReturn(Mono.just(false));
        when(repository.save(any())).thenAnswer(invocation -> {
            MedicalAppointment ma = invocation.getArgument(0);
            ma.setId(1L);
            ma.setVersion(0L);
            return Mono.just(ma);
        });

        StepVerifier.create(service.createAppointment(request, null))
                .assertNext(resp -> assertEquals(start.plusMinutes(50), resp.endsAt()))
                .verifyComplete();

        ArgumentCaptor<MedicalAppointment> captor = ArgumentCaptor.forClass(MedicalAppointment.class);
        verify(repository).save(captor.capture());
        MedicalAppointment saved = captor.getValue();
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void createAppointmentRejectsOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        CreateAppointmentRequest request = baseRequest(start);

        when(repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(eq(99L), any(), any()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.createAppointment(request, null))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void usesPatientProfileWhenPatientIdProvided() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                99L,
                Specialty.PSICOLOGIA,
                start,
                null,
                null,
                DocumentType.DNI,
                "12345678",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true
        );

        when(patientProfileService.findById(5L))
                .thenReturn(Mono.just(new PatientProfile("Ana", "Lopez", "ana@example.com", "+34123456789")));
        when(repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(eq(99L), any(), any()))
                .thenReturn(Mono.just(false));
        when(repository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createAppointment(request, 5L))
                .assertNext(resp -> {
                    assertEquals("Ana", resp.firstName());
                    assertEquals("Lopez", resp.lastName());
                    assertEquals("ana@example.com", resp.email());
                    assertEquals("+34123456789", resp.phone());
                })
                .verifyComplete();
    }

    @Test
    void requiresPatientDataWhenNoPatientId() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                99L,
                Specialty.PSICOLOGIA,
                start,
                null,
                "Perez",
                DocumentType.DNI,
                "12345678",
                "juan@example.com",
                "+123456789",
                null,
                null,
                null,
                null,
                null,
                true
        );

        StepVerifier.create(service.createAppointment(request, null))
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, never()).save(any());
    }

    @Test
    void rejectsWhenPrivacyConsentFalse() {
        LocalDateTime start = LocalDateTime.now().plusDays(4);
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                99L,
                Specialty.PSICOLOGIA,
                start,
                "Juan",
                "Perez",
                DocumentType.DNI,
                "12345678",
                "juan@example.com",
                "+123456789",
                null,
                null,
                null,
                null,
                null,
                false
        );

        StepVerifier.create(service.createAppointment(request, null))
                .expectError(ResponseStatusException.class)
                .verify();

        verifyNoInteractions(repository);
    }
}*/
