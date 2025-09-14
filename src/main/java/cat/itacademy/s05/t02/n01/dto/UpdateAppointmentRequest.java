package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateAppointmentRequest(

        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        LocalDateTime startsAt,

        LocalDateTime endsAt,

        @NotNull(message = "El estado es obligatorio")
        AppointmentStatus status,

        @Email String email,

        String notes
) {}
