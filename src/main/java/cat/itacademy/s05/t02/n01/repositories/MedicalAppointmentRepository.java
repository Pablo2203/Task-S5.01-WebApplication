
package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MedicalAppointmentRepository extends ReactiveCrudRepository<MedicalAppointment, Long> {

    Mono<Boolean> existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(Long professionalId,
                                                                                LocalDateTime endsAt,
                                                                                LocalDateTime startsAt);

    // para detectar solapamientos de citas de un mismo profesional comparando rangos de tiempo
}


