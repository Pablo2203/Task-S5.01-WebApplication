/*
package cat.itacademy.s05.t02.n01.dto;

import cat.itacademy.s05.t02.n01.enums.DocumentType;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull(message = "El ID del profesional es obligatorio")
        Long professionalId,

        @NotNull(message = "La especialidad es obligatoria")
        Specialty specialty,

        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        LocalDateTime startsAt,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
        String lastName,

        @NotNull(message = "El tipo de documento es obligatorio")
        DocumentType documentType,

        @NotBlank(message = "El número de documento es obligatorio")
        @Pattern(regexp = "^[0-9]{7,10}$", message = "El documento debe tener entre 7 y 10 dígitos")
        String documentNumber,

        @Email(message = "El email no es válido")
        @Size(max = 150)
        String email,

        @Pattern(regexp = "^[0-9+()\\s-]{6,20}$", message = "El teléfono no es válido")
        String phone,

        @Size(max = 100, message = "La obra social no puede superar los 100 caracteres")
        String healthInsurance,

        @Size(max = 50, message = "El plan de la obra social no puede superar los 50 caracteres")
        String healthPlan,

        @Size(max = 50, message = "El número de afiliado no puede superar los 50 caracteres")
        String affiliateNumber,

        @Size(max = 255, message = "El asunto no puede superar los 255 caracteres")
        String subject,

        String message,

        @AssertTrue(message = "Debe aceptar las políticas de privacidad")
        boolean privacyConsent
) {}
*/
