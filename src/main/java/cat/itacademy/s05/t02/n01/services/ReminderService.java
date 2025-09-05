package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final MedicalAppointmentRepository repository;

    @Value("${app.reminders.enabled:true}")
    private boolean enabled;

    @Value("${app.reminders.hours-before:24}")
    private long hoursBefore;

    // Ejecuta cada 5 minutos (configurable si querés)
    @Scheduled(fixedDelayString = "${app.reminders.fixed-delay-ms:300000}")
    public void run() {
        if (!enabled) return;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plusHours(hoursBefore);

        repository.findByStatusAndStartsAtBetweenAndReminderSentAtIsNull(
                        AppointmentStatus.SCHEDULED, now, until)
                .flatMap(this::sendReminder)
                .onErrorContinue((ex, o) -> log.warn("Reminder error: {}", ex.toString()))
                .subscribe();
    }

    private Mono<MedicalAppointment> sendReminder(MedicalAppointment appt) {
        String message = buildMessage(appt);
        String phone = appt.getPhone() != null ? appt.getPhone() : "";
        String waLink = "https://wa.me/" + phone + "?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        // Noop sender: log y marcamos como enviado
        log.info("[REMINDER] to={} startsAt={} link={}", phone, appt.getStartsAt(), waLink);

        appt.setReminderSentAt(LocalDateTime.now());
        appt.setUpdatedAt(LocalDateTime.now());
        return repository.save(appt);
    }

    private String buildMessage(MedicalAppointment appt) {
        String nombre = appt.getFirstName();
        String especialidad = appt.getSpecialty() != null ? appt.getSpecialty().name().toLowerCase() : "";
        if (!especialidad.isEmpty()) {
            especialidad = Character.toUpperCase(especialidad.charAt(0)) + especialidad.substring(1);
        }
        String fecha = appt.getStartsAt() != null ? appt.getStartsAt().format(DateTimeFormatter.ofPattern("dd/MM")) : "";
        String hora = appt.getStartsAt() != null ? appt.getStartsAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "";

        return "Hola " + nombre + ", te recordamos tu turno de " + especialidad +
                (appt.getProfessionalId() != null ? " con su profesional asignado" : "") +
                " el " + fecha + " a las " + hora +
                " en YRIGOYEN Consultorios Médicos (Hipólito Yrigoyen 261, Monte Grande). " +
                "Por favor, confirmá tu asistencia respondiendo este mensaje. Tel: +54 11 4296-4063.";
    }
}

