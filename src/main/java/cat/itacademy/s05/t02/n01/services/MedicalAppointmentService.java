package cat.itacademy.s05.t02.n01.services;

public class MedicalAppointmentService {
}

/*Reglas que van en el service (no en el controller)
endsAt = startsAt + duración por defecto (ej. 50 min) si no viene.

Validar solapamientos del profesional (consulta por rango antes de crear).

Si viene patientId, completar firstName/lastName/email/phone con datos del perfil al momento de reservar (snapshot).

Si no viene patientId, los datos personales son requeridos.

Si privacyConsent es false, rechazar 400.

Setear createdAt/updatedAt y manejar @Version para updates.*/


