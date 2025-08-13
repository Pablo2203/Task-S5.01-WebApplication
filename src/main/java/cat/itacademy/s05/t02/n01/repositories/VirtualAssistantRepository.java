package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface VirtualAssistantRepository extends ReactiveCrudRepository<VirtualAssistant, Long> {
}