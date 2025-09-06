package cat.itacademy.s05.t02.n01.mapper;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequestPublic;
import cat.itacademy.s05.t02.n01.dto.UpdateAppointmentRequest;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    // Solicitud pública → entidad REQUESTED
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientId", ignore = true) // aún no asociado a paciente
    @Mapping(target = "professionalId", ignore = true)
    @Mapping(target = "endsAt", ignore = true)
    @Mapping(target = "startsAt", ignore = true)
    @Mapping(target = "healthPlan", ignore = true)
    @Mapping(target = "affiliateNumber", ignore = true)
    @Mapping(target = "reminderSentAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "REQUESTED")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    MedicalAppointment toEntityFromPublicRequest(CreateAppointmentRequestPublic dto);

    AppointmentResponse toResponse(MedicalAppointment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDto(UpdateAppointmentRequest dto, @MappingTarget MedicalAppointment entity);
}
