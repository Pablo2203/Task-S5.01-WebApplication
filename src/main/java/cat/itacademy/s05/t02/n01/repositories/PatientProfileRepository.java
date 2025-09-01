   package cat.itacademy.s05.t02.n01.repositories;

   import cat.itacademy.s05.t02.n01.dto.PatientProfile;
   import org.springframework.data.repository.reactive.ReactiveCrudRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface PatientProfileRepository extends ReactiveCrudRepository<PatientProfile, Long> {
       // Método básico heredado de ReactiveCrudRepository
   }