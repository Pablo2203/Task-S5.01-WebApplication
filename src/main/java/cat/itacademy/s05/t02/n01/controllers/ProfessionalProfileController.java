package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.ProfessionalProfileRequest;
import cat.itacademy.s05.t02.n01.dto.ProfessionalProfileResponse;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import cat.itacademy.s05.t02.n01.services.ProfessionalProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProfessionalProfileController {

    private final ProfessionalProfileService service;
    private final UserRepository users;

    @GetMapping(path = "/api/professional/me/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('PROFESSIONAL','ADMIN')")
    public Mono<ProfessionalProfileResponse> getMyProfile(Authentication auth) {
        String username = auth.getName();
        return users.findByUsername(username).flatMap(u -> service.getByUserId(u.getId()));
    }

    @PutMapping(path = "/api/professional/me/profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('PROFESSIONAL','ADMIN')")
    public Mono<ProfessionalProfileResponse> updateMyProfile(Authentication auth, @RequestBody ProfessionalProfileRequest req) {
        String username = auth.getName();
        return users.findByUsername(username).flatMap(u -> service.upsert(u.getId(), req));
    }

    @GetMapping(path = "/api/admin/professionals/{userId}/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ProfessionalProfileResponse> getProfileByAdmin(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @PutMapping(path = "/api/admin/professionals/{userId}/profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ProfessionalProfileResponse> updateProfileByAdmin(@PathVariable Long userId, @RequestBody ProfessionalProfileRequest req) {
        return service.upsert(userId, req);
    }
}

