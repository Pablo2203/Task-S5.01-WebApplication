
package cat.itacademy.s05.t02.n01.model;

import cat.itacademy.s05.t02.n01.enums.CoverageType;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.enums.Specialty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("medical_appointments")
public class MedicalAppointment {

    @Id
    private Long id;

    // Relación con dominio
    @Column("patient_id")
    @Nullable
    private Long patientId;

    @Column("professional_id")
    @Nullable
    private Long professionalId;

    @Column("specialty")
    @NotNull
    private Specialty specialty; // enum del dominio (psicología, psiquiatría, etc.)

    @Column("starts_at")
    @Nullable
    private LocalDateTime startsAt;

    @Column("ends_at")
    private LocalDateTime endsAt;

    @Column("status")
    @NotNull
    private AppointmentStatus status; // SCHEDULED/CANCELLED/COMPLETED/NO_SHOW

    // Snapshot de datos del paciente en el momento de reservar
    @Column("first_name")
    @NotBlank
    private String firstName;

    @Column("last_name")
    @NotBlank
    private String lastName;

    @Column("email")
    @Email
    private String email;

    @Column("phone")
    @Pattern(regexp = "^[0-9+()\\s-]{6,20}$")
    private String phone;

    @Column("coverage_type")
    private CoverageType coverageType; // INSURANCE o PRIVATE

    @Column("health_insurance")
    private String healthInsurance; // nombre de la obra social informada

    @Column("health_plan")
    private String healthPlan; // plan/planilla/opcional

    @Column("affiliate_number")
    private String affiliateNumber; // nro de afiliado (si aplica)

    // Motivo/mensaje de la reserva (como en tu print)
    @Column("subject")
    private String subject;

    @Column("message")
    private String message;

    @Column("preferred_professional")
    private String preferredProfessional;

    // Auditoría mínima
    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("reminder_sent_at")
    private LocalDateTime reminderSentAt;

    @Version
    private Long version;
}
