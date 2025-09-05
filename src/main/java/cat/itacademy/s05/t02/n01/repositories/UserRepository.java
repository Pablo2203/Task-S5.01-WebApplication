package cat.itacademy.s05.t02.n01.repositories;

import cat.itacademy.s05.t02.n01.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository  extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);
    Flux<User> findByRole(String role);
}
