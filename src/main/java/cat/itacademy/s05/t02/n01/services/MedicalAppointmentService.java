package cat.itacademy.s05.t02.n01.services;
import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequest;
import cat.itacademy.s05.t02.n01.model.PatientProfile;
import cat.itacademy.s05.t02.n01.mapper.AppointmentMapper;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MedicalAppointmentService {

    private static final long DEFAULT_DURATION_MINUTES = 50L;
    private final MedicalAppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PatientProfileService patientProfileService;

    public Mono<AppointmentResponse> createAppointment(CreateAppointmentRequest request, Long patientId) {
        if (!request.privacyConsent()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe aceptar las políticas de privacidad"));
        }

        MedicalAppointment entity = mapper.toEntity(request, patientId);
        if (entity.getEndsAt() == null) {
            entity.setEndsAt(entity.getStartsAt().plusMinutes(DEFAULT_DURATION_MINUTES));
        }
        Mono<MedicalAppointment> prepared;
        if (patientId != null) {
            PatientProfile profile = patientProfileService.findById(patientId);
            prepared = Mono.just(applyProfile(entity, profile));
        } else {
            if (!hasPatientData(entity)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos del paciente requeridos"));
            }
            prepared = Mono.just(entity);
        }
        return prepared
                .flatMap(app -> repository
                        .existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(app.getProfessionalId(), app.getEndsAt(), app.getStartsAt())
                        .flatMap(exists -> exists
                                ? Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El profesional ya tiene un turno en ese horario"))
                                : repository.save(app)))
                .map(mapper::toResponse);
    }
    private MedicalAppointment applyProfile(MedicalAppointment entity, PatientProfile profile) {
        entity.setFirstName(profile.getFirstName());
        entity.setLastName(profile.getLastName());
        entity.setEmail(profile.getEmail());
        entity.setPhone(profile.getPhone());
        return entity;
    }

    private boolean hasPatientData(MedicalAppointment entity) {
        return StringUtils.hasText(entity.getFirstName())
                && StringUtils.hasText(entity.getLastName())
                && StringUtils.hasText(entity.getEmail())
                && StringUtils.hasText(entity.getPhone());
    }
}

