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
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalAppointmentService {

    private static final long DEFAULT_DURATION_MINUTES = 50L;

    private final MedicalAppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final UserRepository users;

    // 1) Crear solicitud pública (REQUESTED)
    public Mono<AppointmentResponse> createRequest(CreateAppointmentRequestPublic request) {
        log.info("Creating REQUESTED appointment subject={} coverage={} ", request.subject(), request.coverageType());
        if (request.coverageType() == CoverageType.INSURANCE && (request.healthInsurance() == null || request.healthInsurance().isBlank())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar su Obra Social"));
        }

        MedicalAppointment entity = mapper.toEntityFromPublicRequest(request);
        // specialty, coverageType, healthInsurance, datos de contacto vienen del mapper por nombre

        return repository.save(entity).map(mapper::toResponse);
    }

    // 2) Agendar una solicitud (pasar a SCHEDULED)
    public Mono<AppointmentResponse> scheduleAppointment(Long id, ScheduleAppointmentRequest dto) {
        log.info("Scheduling appointment id={} professionalId={} startsAt={}", id, dto.professionalId(), dto.startsAt());
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
                                    log.warn("Overlap detected professionalId={} startsAt={} endsAt={}", dto.professionalId(), startsAt, endsAt);
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
        log.info("Creating DIRECT appointment professionalId={} startsAt={} firstName={} lastName={} ", dto.professionalId(), dto.startsAt(), dto.firstName(), dto.lastName());
        LocalDateTime startsAt = dto.startsAt();
        LocalDateTime endsAt = dto.endsAt() != null ? dto.endsAt() : startsAt.plusMinutes(DEFAULT_DURATION_MINUTES);
        return repository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(dto.professionalId(), endsAt, startsAt)
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Overlap on direct creation professionalId={} startsAt={} endsAt={}", dto.professionalId(), startsAt, endsAt);
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
                    Mono<MedicalAppointment> enriched = (dto.email() != null && !dto.email().isBlank())
                            ? users.findByEmail(dto.email())
                                    .map(u -> { a.setPatientId(u.getId()); return a; })
                                    .switchIfEmpty(Mono.just(a))
                            : Mono.just(a);
                    return enriched.flatMap(repository::save)
                            .map(appt -> {
                                log.info("Appointment created id={} professionalId={} startsAt={}", appt.getId(), appt.getProfessionalId(), appt.getStartsAt());
                                return mapper.toResponse(appt);
                            });
                });
    }

    // 4) Actualizar cita (horarios/estado)
    public Mono<AppointmentResponse> updateAppointment(Long id, UpdateAppointmentRequest dto) {
        log.info("Updating appointment id={} startsAt={} status={} ", id, dto.startsAt(), dto.status());
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
                                    log.warn("Overlap on update professionalId={} startsAt={} endsAt={}", professionalId, startsAt, endsAt);
                                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Solapamiento de turnos"));
                                }
                                Mono<MedicalAppointment> emailMono;
                                if (dto.email() != null) {
                                    String newEmail = dto.email();
                                    emailMono = users.findByEmail(newEmail)
                                            .map(u -> {
                                                existing.setEmail(newEmail);
                                                existing.setPatientId(u.getId());
                                                return existing;
                                            })
                                            .switchIfEmpty(Mono.fromCallable(() -> {
                                                existing.setEmail(newEmail);
                                                return existing;
                                            }));
                                } else {
                                    emailMono = Mono.just(existing);
                                }
                                return emailMono.flatMap(ent -> {
                                    ent.setStartsAt(startsAt);
                                    ent.setEndsAt(endsAt);
                                    ent.setStatus(dto.status());
                                    ent.setUpdatedAt(LocalDateTime.now());
                                    return repository.save(ent)
                                            .map(appt -> {
                                                log.info("Appointment updated id={} startsAt={} status={}", appt.getId(), appt.getStartsAt(), appt.getStatus());
                                                return mapper.toResponse(appt);
                                            });
                                });
                            });
                });
    }
}
