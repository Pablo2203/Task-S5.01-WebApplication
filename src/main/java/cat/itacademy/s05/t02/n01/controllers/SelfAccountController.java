package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

record MyAccountResponse(String username, String email, String role) {}
record UpdateAccountRequest(String username, String email) {}
record ChangePasswordRequest(String currentPassword, String newPassword) {}

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class SelfAccountController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @GetMapping
    public Mono<MyAccountResponse> me(Authentication auth) {
        return users.findByUsername(auth.getName())
                .map(u -> new MyAccountResponse(u.getUsername(), u.getEmail(), u.getRole()));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MyAccountResponse> update(Authentication auth, @RequestBody UpdateAccountRequest body) {
        String newU = body.username();
        String newE = body.email();
        return users.findByUsername(auth.getName())
                .flatMap(u -> {
                    u.setUsername(newU);
                    u.setEmail(newE);
                    return users.save(u);
                })
                .map(u -> new MyAccountResponse(u.getUsername(), u.getEmail(), u.getRole()));
    }

    @PutMapping(path = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> changePassword(Authentication auth, @RequestBody ChangePasswordRequest body) {
        return users.findByUsername(auth.getName())
                .flatMap(u -> {
                    if (!encoder.matches(body.currentPassword(), u.getPasswordHash())) {
                        return Mono.just(ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).<Void>build());
                    }
                    u.setPasswordHash(encoder.encode(body.newPassword()));
                    return users.save(u).thenReturn(ResponseEntity.status(org.springframework.http.HttpStatus.OK).<Void>build());
                })
                .defaultIfEmpty(ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).<Void>build());
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteSelf(Authentication auth) {
        return users.findByUsername(auth.getName())
                .flatMap(u -> users.deleteById(u.getId()).thenReturn(ResponseEntity.status(org.springframework.http.HttpStatus.NO_CONTENT).<Void>build()))
                .defaultIfEmpty(ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).<Void>build());
    }
}
