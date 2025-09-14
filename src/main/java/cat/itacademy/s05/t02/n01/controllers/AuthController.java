package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.LoginRequest;
import cat.itacademy.s05.t02.n01.dto.LoginResponse;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.MailService;
import cat.itacademy.s05.t02.n01.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login con username o email", description = "Devuelve JWT y roles del usuario si las credenciales son válidas")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // Permitir login usando username o email en el mismo campo
        return users.findByUsername(req.username())
                .switchIfEmpty(users.findByEmail(req.username()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                .flatMap(u -> {
                    if (!u.isEnabled() || !passwordEncoder.matches(req.password(), u.getPasswordHash())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
                    }
                    log.info("Login OK username={} role={}", u.getUsername(), u.getRole());
                    List<String> roles = List.of(u.getRole());
                    String token = jwtService.generateToken(u.getUsername(), roles);
                    Instant expires = Instant.now().plusSeconds(60L * 60L);
                    return Mono.just(new LoginResponse(token, roles, expires));
                });
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registro de usuario", description = "Crea cuenta de PATIENT habilitada o PROFESSIONAL pendiente de aprobación")
    public Mono<ResponseEntity<java.util.Map<String, Object>>> register(@Valid @RequestBody RegisterRequest req) {
        log.info("Register attempt username={} email={} role={} ", req.username(), req.email(), req.roleWanted());
        return users.findByUsername(req.username())
                .flatMap(u -> Mono.<cat.itacademy.s05.t02.n01.model.User>error(new ResponseStatusException(HttpStatus.CONFLICT, "Usuario ya existe")))
                .switchIfEmpty(users.findByEmail(req.email())
                        .flatMap(u -> Mono.<cat.itacademy.s05.t02.n01.model.User>error(new ResponseStatusException(HttpStatus.CONFLICT, "Email ya existe"))))
                .switchIfEmpty(Mono.defer(() -> {
                    var role = req.roleWanted().equals("PROFESSIONAL") ? "PROFESSIONAL" : "PATIENT";
                    var enabled = !role.equals("PROFESSIONAL");
                    cat.itacademy.s05.t02.n01.model.User u = new cat.itacademy.s05.t02.n01.model.User();
                    u.setUsername(req.username());
                    u.setEmail(req.email());
                    u.setPasswordHash(passwordEncoder.encode(req.password()));
                    u.setRole(role);
                    u.setEnabled(enabled);
                    return users.save(u);
                }))
                .doOnSuccess(saved -> {
                    try {
                        String token = jwtService.generateTokenWithPurpose(saved.getEmail(), List.of(), "confirm", 60 * 24);
                        String link = frontendBaseUrl.replaceAll("/$", "") + "/confirmar-email?token=" + token;
                        mailService.send(saved.getEmail(), "Confirmá tu cuenta", "Hola, hacé click para confirmar tu cuenta: " + link);
                        log.info("Register OK userId={} role={} emailSent=true", saved.getId(), saved.getRole());
                    } catch (Exception ignored) {}
                })
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("ok", true)));
    }

    @PostMapping(path = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Solicitud de reseteo de contraseña")
    public Mono<ResponseEntity<Void>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        return users.findByEmail(email)
                .doOnNext(u -> {
                    try {
                        String token = jwtService.generateTokenWithPurpose(u.getUsername(), List.of(u.getRole()), "reset", 60);
                        String link = frontendBaseUrl.replaceAll("/$", "") + "/reset-password?token=" + token;
                        mailService.send(email, "Restablecer contraseña", "Usá este enlace para restablecer tu contraseña: " + link);
                    } catch (Exception ignored) {}
                })
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping(path = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Restablecer contraseña con token")
    public Mono<Void> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.getOrDefault("token", "");
        String newPass = body.getOrDefault("password", "");
        var claims = jwtService.validate(token);
        String purpose = String.valueOf(claims.getClaim("purpose"));
        if (!"reset".equals(purpose)) return Mono.empty();
        String username = claims.getSubject();
        return users.findByUsername(username)
                .flatMap(u -> {
                    u.setPasswordHash(passwordEncoder.encode(newPass));
                    return users.save(u).then();
                })
                .then();
    }

    @GetMapping(path = "/confirm-email")
    @Operation(summary = "Confirmar email de registro")
    public Mono<ResponseEntity<Void>> confirmEmail(@RequestParam("token") String token) {
        var claims = jwtService.validate(token);
        String purpose = String.valueOf(claims.getClaim("purpose"));
        if (!"confirm".equals(purpose)) return Mono.just(ResponseEntity.status(400).build());
        String email = claims.getSubject();
        return users.findByEmail(email)
                .flatMap(u -> {
                    try {
                        var fld = u.getClass().getDeclaredField("emailVerified");
                        fld.setAccessible(true);
                        fld.set(u, true);
                    } catch (Exception ignored) {}
                    return users.save(u);
                })
                .thenReturn(ResponseEntity.status(302)
                        .location(java.net.URI.create(frontendBaseUrl.replaceAll("/$", "") + "/confirmar-email?ok=1"))
                        .build());
    }
}
