package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.model.PatientProfile;
import cat.itacademy.s05.t02.n01.repositories.PatientProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class PatientProfileServiceImpl implements PatientProfileService {

    private final PatientProfileRepository patientProfileRepository;

    // Constructor inyectado automáticamente por Spring
    public PatientProfileServiceImpl(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    @Override
    public Mono<PatientProfile> save(PatientProfile patientProfile) {
        return patientProfileRepository.save(patientProfile);
    }

    @Override
    public Mono<PatientProfile> findById(Long patientId) {
        return patientProfileRepository.findById(patientId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado")));
    }
}
