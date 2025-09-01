/*
package cat.itacademy.s05.t02.n01.mapper;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.UpdateAppointmentRequest;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import org.junit.jupiter.api.Test;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppointmentMapperTest {

    @Autowired
    private AppointmentMapper mapper;

    @Test
    public void testToEntity() {
        CreateAppointmentRequest request = new CreateAppointmentRequest(*/
/* datos de prueba *//*
);
        Long patientId = 1L;

        MedicalAppointment entity = mapper.toEntity(request, patientId);

        assertNotNull(entity);
        assertEquals(patientId, entity.getPatientId());
        assertEquals("SCHEDULED", entity.getStatus());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }
}*/
