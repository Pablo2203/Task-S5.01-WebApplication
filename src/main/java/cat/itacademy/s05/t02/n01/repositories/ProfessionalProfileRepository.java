package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.model.ProfessionalProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface ProfessionalProfileRepository extends ReactiveCrudRepository<ProfessionalProfile, Long> {
    Mono<ProfessionalProfile> findByUserId(Long userId);
    Flux<ProfessionalProfile> findBySpecialty(String specialty);
}
