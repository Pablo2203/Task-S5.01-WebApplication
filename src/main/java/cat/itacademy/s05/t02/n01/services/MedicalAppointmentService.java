package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequestPublic;
import cat.itacademy.s05.t02.n01.dto.DirectAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.UpdateAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.ScheduleAppointmentRequest;
import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.enums.CoverageType;
import cat.itacademy.s05.t02.n01.mapper.AppointmentMapper;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicalAppointmentService {

    private static final long DEFAULT_DURATION_MINUTES = 50L;

    private final MedicalAppointmentRepository repository;
    private final AppointmentMapper mapper;

    // 1) Crear solicitud pública (REQUESTED)
    public Mono<AppointmentResponse> createRequest(CreateAppointmentRequestPublic request) {
        if (request.coverageType() == CoverageType.INSURANCE && (request.healthInsurance() == null || request.healthInsurance().isBlank())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar su Obra Social"));
        }

        MedicalAppointment entity = mapper.toEntityFromPublicRequest(request);
        // specialty, coverageType, healthInsurance, datos de contacto vienen del mapper por nombre

        return repository.save(entity).map(mapper::toResponse);
    }

    // 2) Agendar una solicitud (pasar a SCHEDULED)
    public Mono<AppointmentResponse> scheduleAppointment(Long id, ScheduleAppointmentRequest dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada")))
                .flatMap(existing -> {
                    if (existing.getStatus() != AppointmentStatus.REQUESTED) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden agendar solicitudes en estado REQUESTED"));
                    }

                    LocalDateTime startsAt = dto.startsAt();
                    LocalDateTime endsAt = dto.endsAt() != null ? dto.endsAt() : startsAt.plusMinutes(DEFAULT_DURATION_MINUTES);

                    // Validación de solapamiento
                    return repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(dto.professionalId(), endsAt, startsAt)
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya tiene un turno en ese horario"));
                                }

                                existing.setProfessionalId(dto.professionalId());
                                existing.setStartsAt(startsAt);
                                existing.setEndsAt(endsAt);
                                existing.setStatus(AppointmentStatus.SCHEDULED);
                                existing.setUpdatedAt(LocalDateTime.now());

                                return repository.save(existing).map(mapper::toResponse);
                            });
                });
    }

    // 3) Crear cita directa (ADMIN o PROFESSIONAL) → estado SCHEDULED
    public Mono<AppointmentResponse> createDirect(DirectAppointmentRequest dto) {
        LocalDateTime startsAt = dto.startsAt();
        LocalDateTime endsAt = dto.endsAt() != null ? dto.endsAt() : startsAt.plusMinutes(DEFAULT_DURATION_MINUTES);
        return repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(dto.professionalId(), endsAt, startsAt)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya tiene un turno en ese horario"));
                    }
                    MedicalAppointment a = MedicalAppointment.builder()
                            .professionalId(dto.professionalId())
                            .specialty(dto.specialty())
                            .startsAt(startsAt)
                            .endsAt(endsAt)
                            .status(AppointmentStatus.SCHEDULED)
                            .firstName(dto.firstName())
                            .lastName(dto.lastName())
                            .email(dto.email())
                            .phone(dto.phone())
                            .coverageType(dto.coverageType())
                            .healthInsurance(dto.healthInsurance())
                            .healthPlan(dto.healthPlan())
                            .affiliateNumber(dto.affiliateNumber())
                            .subject(dto.subject())
                            .message(dto.message())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return repository.save(a).map(mapper::toResponse);
                });
    }

    // 4) Actualizar cita (horarios/estado)
    public Mono<AppointmentResponse> updateAppointment(Long id, UpdateAppointmentRequest dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada")))
                .flatMap(existing -> {
                    LocalDateTime startsAt = dto.startsAt();
                    LocalDateTime endsAt = dto.endsAt() != null ? dto.endsAt() : (existing.getStartsAt() != null ? existing.getStartsAt().plusMinutes(DEFAULT_DURATION_MINUTES) : null);
                    Long professionalId = existing.getProfessionalId();
                    if (professionalId == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cita no tiene profesional asignado"));
                    }
                    return repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(professionalId, endsAt, startsAt)
                            .flatMap(exists -> {
                                if (exists && !startsAt.equals(existing.getStartsAt())) {
                                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Solapamiento de turnos"));
                                }
                                existing.setStartsAt(startsAt);
                                existing.setEndsAt(endsAt);
                                existing.setStatus(dto.status());
                                existing.setUpdatedAt(LocalDateTime.now());
                                return repository.save(existing).map(mapper::toResponse);
                            });
                });
    }
}
