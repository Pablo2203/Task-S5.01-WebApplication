
package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MedicalAppointmentRepository extends ReactiveCrudRepository<MedicalAppointment, Long> {

    Mono<Boolean> existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(Long professionalId,
                                                                                LocalDateTime endsAt,
                                                                                LocalDateTime startsAt);

    // para detectar solapamientos de citas de un mismo profesional comparando rangos de tiempo

    Flux<MedicalAppointment> findByStatus(AppointmentStatus status);

    Flux<MedicalAppointment> findByStatusAndStartsAtBetweenAndReminderSentAtIsNull(
            AppointmentStatus status,
            LocalDateTime startsAtFrom,
            LocalDateTime startsAtTo
    );

    Flux<MedicalAppointment> findByProfessionalIdAndStatusAndStartsAtBetween(
            Long professionalId,
            AppointmentStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
    Flux<MedicalAppointment> findByPatientIdAndStatusAndStartsAtBetween(
            Long patientId,
            AppointmentStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

}
