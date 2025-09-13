package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record UserSummary(Long id, String username, String email, boolean enabled, String role) {}

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUsersController {

    private final UserRepository users;

    @GetMapping("/professionals/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserSummary> getPendingProfessionals() {
        return users.findByRoleAndEnabled("PROFESSIONAL", false)
                .map(this::toSummary);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserSummary> approve(@PathVariable Long id) {
        return users.findById(id)
                .flatMap(u -> { u.setEnabled(true); return users.save(u); })
                .map(this::toSummary);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> delete(@PathVariable Long id) {
        return users.deleteById(id);
    }

    private UserSummary toSummary(User u) {
        return new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.isEnabled(), u.getRole());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserSummary> listAll() {
        return users.findAll().map(this::toSummary);
    }
}
