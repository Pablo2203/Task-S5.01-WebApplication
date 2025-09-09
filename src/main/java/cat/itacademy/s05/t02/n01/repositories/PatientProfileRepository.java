   package cat.itacademy.s05.t02.n01.repositories;

   import cat.itacademy.s05.t02.n01.model.PatientProfile;
   import org.springframework.data.repository.CrudRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface PatientProfileRepository extends CrudRepository<PatientProfile, Long> {
      // Métodos básicos heredados de CrudRepository
   }