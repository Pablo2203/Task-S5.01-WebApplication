package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.CoverageType;
import cat.itacademy.s05.t02.n01.enums.HealthInsurance;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record CreateAppointmentRequestPublic(

        @NotBlank @Size(max = 100)
        String firstName,

        @NotBlank @Size(max = 100)
        String lastName,

        @NotBlank @Email @Size(max = 150)
        String email,

        @NotBlank
        @Pattern(regexp = "^[0-9+()\\s-]{6,20}$")
        String phone,

        @NotNull
        CoverageType coverageType,

        @Nullable
        HealthInsurance healthInsurance,   // requerido solo si coverageType=INSURANCE (validación lógica en service)

        @NotNull
        Specialty specialty,

        @Size(max = 255)
        String subject,

        String message
) {}
