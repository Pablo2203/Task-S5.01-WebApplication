package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MedicalAppointmentRepository extends ReactiveCrudRepository<MedicalAppointment, Long> {
}