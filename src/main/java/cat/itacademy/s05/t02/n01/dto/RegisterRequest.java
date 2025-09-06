package cat.itacademy.s05.t02.n01.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50)
        String username,
        @NotBlank @Email @Size(max = 150)
        String email,
        @NotBlank @Size(min = 6, max = 100)
        String password,
        @NotBlank @Pattern(regexp = "^(PATIENT|PROFESSIONAL)$")
        String roleWanted
) {}

