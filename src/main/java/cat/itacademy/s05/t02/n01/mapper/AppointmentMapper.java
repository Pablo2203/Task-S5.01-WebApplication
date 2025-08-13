package cat.itacademy.s05.t02.n01.mapper;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequest;
import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;

import java.time.LocalDateTime;

public class AppointmentMapper {

    // Convierte el DTO de creación + el patientId (si está logueado) a ENTIDAD lista para persistir
    public static MedicalAppointment toEntity(CreateAppointmentRequest dto, Long patientId) {
        return MedicalAppointment.builder()
                .patientId(patientId)                  // viene del contexto de seguridad o null si invitado
                .professionalId(dto.professionalId())  // desde el DTO
                .specialty(dto.specialty())
                .startsAt(dto.startsAt())
                .status(AppointmentStatus.SCHEDULED)   // por defecto al crear
                // snapshot de datos personales (los “congelamos” en la cita)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .documentType(dto.documentType())
                .documentNumber(dto.documentNumber())
                .email(dto.email())
                .phone(dto.phone())
                .healthInsurance(dto.healthInsurance())
                .healthPlan(dto.healthPlan())
                .affiliateNumber(dto.affiliateNumber())
                .subject(dto.subject())
                .message(dto.message())
                .privacyConsent(dto.privacyConsent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Convierte ENTIDAD → DTO de respuesta (lo que mandás al front)
    public static AppointmentResponse toResponse(MedicalAppointment e) {
        return new AppointmentResponse(
                e.getId(),
                e.getProfessionalId(),
                e.getSpecialty(),
                e.getStartsAt(),
                e.getEndsAt(),
                e.getStatus(),
                e.getFirstName(),
                e.getLastName(),
                e.getDocumentType(),
                e.getDocumentNumber(),
                e.getEmail(),
                e.getPhone(),
                e.getHealthInsurance(),
                e.getHealthPlan(),
                e.getAffiliateNumber(),
                e.getSubject(),
                e.getMessage()
        );
    }
}
