package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import cat.itacademy.s05.t02.n01.enums.CoverageType;
import cat.itacademy.s05.t02.n01.enums.HealthInsurance;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long professionalId,
        Specialty specialty,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        AppointmentStatus status,
        String firstName,
        String lastName,
        String email,
        String phone,
        CoverageType coverageType,
        HealthInsurance healthInsurance,
        String subject,
        String message
) {}
