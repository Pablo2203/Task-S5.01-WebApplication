package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import cat.itacademy.s05.t02.n01.enums.DocumentType;
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
        DocumentType documentType,
        String documentNumber,
        String email,
        String phone,
        String healthInsurance,
        String healthPlan,
        String affiliateNumber,
        String subject,
        String message
) {}