package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.model.PatientProfile;
import reactor.core.publisher.Mono;

public interface PatientProfileService {

    PatientProfile save(PatientProfile patientProfile);

    PatientProfile findById(Long patientId);
}

