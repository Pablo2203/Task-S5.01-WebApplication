package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.CoverageType;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record DirectAppointmentRequest(
        @NotNull Long professionalId,
        @NotNull Specialty specialty,
        @NotNull LocalDateTime startsAt,
        LocalDateTime endsAt,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email,
        @Pattern(regexp = "^[0-9+()\\s-]{6,20}$") String phone,
        CoverageType coverageType,
        String healthInsurance,
        String healthPlan,
        String affiliateNumber,
        String subject,
        String message
) {}

