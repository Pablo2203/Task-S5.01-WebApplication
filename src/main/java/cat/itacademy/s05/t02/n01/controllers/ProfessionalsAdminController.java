package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.ProfessionalSummary;
import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/professionals")
public class ProfessionalsAdminController {

    private final UserRepository users;

    @GetMapping
    public Flux<ProfessionalSummary> list(@RequestParam(name = "specialty", required = false) String specialty) {
        // Hoy no hay vínculo profesional-especialidad en el modelo de usuarios; devolvemos todos con rol PROFESSIONAL
        return users.findByRole("PROFESSIONAL")
                .map(this::toSummary);
    }

    private ProfessionalSummary toSummary(User u) {
        return new ProfessionalSummary(u.getId(), u.getUsername(), u.getEmail());
    }
}

