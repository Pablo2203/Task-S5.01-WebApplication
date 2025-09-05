package cat.itacademy.s05.t02.n01.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ScheduleAppointmentRequest(

        @NotNull
        Long professionalId,

        @NotNull
        LocalDateTime startsAt,

        LocalDateTime endsAt // si es null, el servicio calcula 50'
) {}
