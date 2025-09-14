package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.dto.ProfessionalProfileRequest;
import cat.itacademy.s05.t02.n01.dto.ProfessionalProfileResponse;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import cat.itacademy.s05.t02.n01.model.ProfessionalProfile;
import cat.itacademy.s05.t02.n01.repositories.ProfessionalProfileRepository;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfessionalProfileService {

    private final ProfessionalProfileRepository repo;
    private final UserRepository users;

    public Mono<ProfessionalProfileResponse> getByUserId(Long userId) {
        return repo.findByUserId(userId).map(this::toResponse);
    }

    public Mono<ProfessionalProfileResponse> upsert(Long userId, ProfessionalProfileRequest req) {
        return repo.findByUserId(userId)
                .defaultIfEmpty(ProfessionalProfile.builder().userId(userId).build())
                .flatMap(p -> {
                    p.setFirstName(req.firstName());
                    p.setLastName(req.lastName());
                    p.setStudies(req.studies());
                    p.setSpecialty(parseSpecialty(req.specialty()));
                    p.setBio(req.bio());
                    p.setPhotoUrl(req.photoUrl());
                    p.setUpdatedAt(LocalDateTime.now());
                    return repo.save(p);
                })
                .map(this::toResponse);
    }

    private ProfessionalProfileResponse toResponse(ProfessionalProfile p) {
        return new ProfessionalProfileResponse(
                p.getUserId(),
                p.getFirstName(),
                p.getLastName(),
                p.getStudies(),
                p.getSpecialty() != null ? p.getSpecialty().name() : null,
                p.getBio(),
                p.getPhotoUrl()
        );
    }

    private Specialty parseSpecialty(String value) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null;
        try {
            return Specialty.valueOf(v.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
