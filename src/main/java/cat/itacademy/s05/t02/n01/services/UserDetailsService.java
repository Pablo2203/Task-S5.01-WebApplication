package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public Mono<UserDetails> findByUsername(String username) {
        return repo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRole())
                        .build());
    }
}
