package cat.itacademy.s05.t02.n01.mapper;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.UpdateAppointmentRequest;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true) // El ID lo genera la BD
    @Mapping(target = "status", constant = "SCHEDULED") // Estado inicial por defecto
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "patientId", source = "patientId") // viene de parámetro adicional
    MedicalAppointment toEntity(CreateAppointmentRequest dto, Long patientId);

    AppointmentResponse toResponse(MedicalAppointment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDto(UpdateAppointmentRequest dto, @MappingTarget MedicalAppointment entity);
}

