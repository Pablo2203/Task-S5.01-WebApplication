package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequestPublic;
import cat.itacademy.s05.t02.n01.dto.ScheduleAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.WhatsAppTemplateResponse;
import cat.itacademy.s05.t02.n01.enums.AppointmentStatus;
import cat.itacademy.s05.t02.n01.mapper.AppointmentMapper;
import cat.itacademy.s05.t02.n01.model.MedicalAppointment;
import cat.itacademy.s05.t02.n01.repositories.MedicalAppointmentRepository;
import cat.itacademy.s05.t02.n01.services.MedicalAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
public class MedicalAppointmentController {

    private final MedicalAppointmentService service;
    private final MedicalAppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final cat.itacademy.s05.t02.n01.repositories.UserRepository users;

    // Public: crear solicitud de turno (REQUESTED)
    @PostMapping(value = "/api/appointments/requests", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AppointmentResponse>> createRequest(@RequestBody CreateAppointmentRequestPublic request) {
        return service.createRequest(request)
                .map(resp -> ResponseEntity.created(URI.create("/api/admin/appointments/" + resp.id()))
                        .body(resp));
    }

    // Admin: listar solicitudes en estado REQUESTED
    @GetMapping("/api/admin/appointments/requests")
    public Flux<AppointmentResponse> getRequestedAppointments() {
        return repository.findByStatus(AppointmentStatus.REQUESTED)
                .map(mapper::toResponse);
    }

    // Admin: agendar una solicitud
    @PatchMapping(value = "/api/admin/appointments/{id}/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AppointmentResponse> schedule(@PathVariable Long id, @RequestBody ScheduleAppointmentRequest request) {
        return service.scheduleAppointment(id, request);
    }

    // Admin: plantilla de recordatorio WhatsApp para una cita
    @GetMapping("/api/admin/appointments/{id}/whatsapp-template")
    public Mono<WhatsAppTemplateResponse> getWhatsAppTemplate(@PathVariable Long id) {
        return repository.findById(id).map(this::toWhatsAppTemplate);
    }

    // Professional: listar citas propias (SCHEDULED) en rango
    @GetMapping("/api/professional/appointments")
    public Flux<AppointmentResponse> getMyAppointments(Authentication auth,
                                                       @RequestParam("from") String from,
                                                       @RequestParam("to") String to) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMapMany(u -> repository.findByProfessionalIdAndStatusAndStartsAtBetween(u.getId(), AppointmentStatus.SCHEDULED, fromDt, toDt))
                .map(mapper::toResponse);
    }

    // Admin: listar citas de un profesional por rango
    @GetMapping("/api/admin/professionals/{userId}/appointments")
    public Flux<AppointmentResponse> getAppointmentsByProfessional(@PathVariable Long userId,
                                                                   @RequestParam("from") String from,
                                                                   @RequestParam("to") String to) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        return repository.findByProfessionalIdAndStatusAndStartsAtBetween(userId, AppointmentStatus.SCHEDULED, fromDt, toDt)
                .map(mapper::toResponse);
    }

    private WhatsAppTemplateResponse toWhatsAppTemplate(MedicalAppointment appt) {
        String nombre = appt.getFirstName();
        String especialidad = appt.getSpecialty() != null ? appt.getSpecialty().name().toLowerCase() : "";
        if (!especialidad.isEmpty()) {
            especialidad = Character.toUpperCase(especialidad.charAt(0)) + especialidad.substring(1);
        }
        String fecha = appt.getStartsAt() != null ? appt.getStartsAt().format(DateTimeFormatter.ofPattern("dd/MM")) : "";
        String hora = appt.getStartsAt() != null ? appt.getStartsAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "";

        String base = "Hola " + nombre + ", te recordamos tu turno de " + especialidad +
                (appt.getProfessionalId() != null ? " con su profesional asignado" : "") +
                " el " + fecha + " a las " + hora +
                " en YRIGOYEN Consultorios Médicos (Hipólito Yrigoyen 261, Monte Grande). " +
                "Por favor, confirmá tu asistencia respondiendo este mensaje. Tel: +54 11 4296-4063.";

        String phone = appt.getPhone() != null ? appt.getPhone() : "";
        String encoded = URLEncoder.encode(base, StandardCharsets.UTF_8);
        String waLink = "https://wa.me/" + phone + "?text=" + encoded;
        return new WhatsAppTemplateResponse(base, waLink);
    }
}
