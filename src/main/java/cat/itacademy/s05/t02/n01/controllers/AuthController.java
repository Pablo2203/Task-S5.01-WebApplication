package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return users.findByUsername(req.username())
                .switchIfEmpty(Mono.error(new RuntimeException("Credenciales inválidas")))
                .flatMap(u -> {
                    if (!u.isEnabled() || !passwordEncoder.matches(req.password(), u.getPasswordHash())) {
                        return Mono.error(new RuntimeException("Credenciales inválidas"));
                    }
                    List<String> roles = List.of(u.getRole());
                    String token = jwtService.generateToken(u.getUsername(), roles);
                    Instant expires = Instant.now().plusSeconds(60L * 60L);
                    return Mono.just(new LoginResponse(token, roles, expires));
                });
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> register(@Valid @RequestBody RegisterRequest req) {
        return users.findByUsername(req.username())
                .flatMap(u -> Mono.error(new RuntimeException("Usuario ya existe")))
                .switchIfEmpty(users.findByEmail(req.email()).flatMap(u -> Mono.error(new RuntimeException("Email ya existe"))))
                .switchIfEmpty(Mono.defer(() -> {
                    var role = req.roleWanted().equals("PROFESSIONAL") ? "PROFESSIONAL" : "PATIENT";
                    var enabled = !role.equals("PROFESSIONAL");
                    cat.itacademy.s05.t02.n01.model.User u = new cat.itacademy.s05.t02.n01.model.User();
                    u.setUsername(req.username());
                    u.setEmail(req.email());
                    u.setPasswordHash(passwordEncoder.encode(req.password()));
                    u.setRole(role);
                    u.setEnabled(enabled);
                    return users.save(u).then();
                }));
    }
}
