package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.dto.AppointmentResponse;
import cat.itacademy.s05.t02.n01.dto.CreateAppointmentRequestPublic;
import cat.itacademy.s05.t02.n01.dto.ScheduleAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.DirectAppointmentRequest;
import cat.itacademy.s05.t02.n01.dto.UpdateAppointmentRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;

@RestController
@Tag(name = "Appointments")
@RequiredArgsConstructor
public class MedicalAppointmentController {

    private final MedicalAppointmentService service;
    private final MedicalAppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final cat.itacademy.s05.t02.n01.repositories.UserRepository users;

    // Public: crear solicitud de turno (REQUESTED)
    @PostMapping(value = "/api/appointments/requests", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear solicitud pública de turno")
    public Mono<ResponseEntity<AppointmentResponse>> createRequest(@RequestBody java.util.Map<String, Object> body) {
        CreateAppointmentRequestPublic request = coercePublicRequest(body);
        return service.createRequest(request)
                .map(resp -> ResponseEntity.created(URI.create("/api/admin/appointments/" + resp.id()))
                        .body(resp));
    }

    private CreateAppointmentRequestPublic coercePublicRequest(java.util.Map<String, Object> m) {
        String firstName = str(m.get("firstName"));
        String lastName = str(m.get("lastName"));
        String email = str(m.get("email"));
        String phone = str(m.get("phone"));
        String cov = str(m.get("coverageType"));
        cat.itacademy.s05.t02.n01.enums.CoverageType coverageType = null;
        if (cov != null) {
            try { coverageType = cat.itacademy.s05.t02.n01.enums.CoverageType.valueOf(cov.toUpperCase()); } catch (Exception ignored) {}
        }
        String healthInsurance = str(m.get("healthInsurance"));
        String spec = str(m.get("specialty"));
        cat.itacademy.s05.t02.n01.enums.Specialty specialty = null;
        if (spec != null) {
            try { specialty = cat.itacademy.s05.t02.n01.enums.Specialty.valueOf(spec.toUpperCase()); } catch (Exception ignored) {}
        }
        String preferredProfessional = str(m.get("preferredProfessional"));
        String subject = str(m.get("subject"));
        String message = str(m.get("message"));
        return new CreateAppointmentRequestPublic(
                firstName, lastName, email, phone,
                coverageType,
                healthInsurance,
                specialty,
                preferredProfessional,
                subject,
                message
        );
    }

    private static String str(Object o) { return o == null ? null : String.valueOf(o); }

    // Admin: listar solicitudes en estado REQUESTED
    @GetMapping("/api/admin/appointments/requests")
    @Operation(summary = "Listar solicitudes REQUESTED (ADMIN)")
    public Flux<AppointmentResponse> getRequestedAppointments() {
        return repository.findByStatus(AppointmentStatus.REQUESTED)
                .map(mapper::toResponse);
    }

    // Admin: agendar una solicitud
    @PatchMapping(value = "/api/admin/appointments/{id}/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Agendar una solicitud REQUESTED (ADMIN)")
    public Mono<AppointmentResponse> schedule(@PathVariable Long id, @RequestBody ScheduleAppointmentRequest request) {
        return service.scheduleAppointment(id, request);
    }

    // Admin: crear cita directa SCHEDULED
    @PostMapping(value = "/api/admin/appointments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear cita directa (ADMIN)")
    public Mono<AppointmentResponse> createDirectByAdmin(@RequestBody DirectAppointmentRequest request) {
        return service.createDirect(request);
    }

    // Admin: actualizar cita (horario/estado)
    @PatchMapping(value = "/api/admin/appointments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar cita (ADMIN)")
    public Mono<AppointmentResponse> updateByAdmin(@PathVariable Long id, @RequestBody UpdateAppointmentRequest request) {
        return service.updateAppointment(id, request);
    }

    // Admin: cancelar cita
    @PatchMapping("/api/admin/appointments/{id}/cancel")
    @Operation(summary = "Cancelar cita (ADMIN)")
    public Mono<Void> cancelByAdmin(@PathVariable Long id) {
        return repository.findById(id)
                .doOnNext(a -> {
                    a.setStatus(AppointmentStatus.CANCELLED);
                    a.setUpdatedAt(java.time.LocalDateTime.now());
                })
                .flatMap(repository::save)
                .then();
    }

    // Admin: plantilla de recordatorio WhatsApp para una cita
    @GetMapping("/api/admin/appointments/{id}/whatsapp-template")
    @Operation(summary = "Obtener plantilla WhatsApp para recordatorio (ADMIN)")
    public Mono<WhatsAppTemplateResponse> getWhatsAppTemplate(@PathVariable Long id) {
        return repository.findById(id).map(this::toWhatsAppTemplate);
    }

    // Professional: listar citas propias (SCHEDULED) en rango
    @GetMapping("/api/professional/appointments")
    @Operation(summary = "Listar mis citas SCHEDULED (PROFESSIONAL)")
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
    @Operation(summary = "Listar citas de un profesional (ADMIN)")
    public Flux<AppointmentResponse> getAppointmentsByProfessional(@PathVariable Long userId,
                                                                   @RequestParam("from") String from,
                                                                   @RequestParam("to") String to) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        return repository.findByProfessionalIdAndStatusAndStartsAtBetween(userId, AppointmentStatus.SCHEDULED, fromDt, toDt)
                .map(mapper::toResponse);
    }

    // Professional: export CSV of own appointments
    @GetMapping("/api/professional/appointments/export")
    @Operation(summary = "Exportar CSV de mis citas (PROFESSIONAL)")
    public Mono<ResponseEntity<String>> exportMyAppointmentsCsv(Authentication auth,
                                                                @RequestParam("from") String from,
                                                                @RequestParam("to") String to) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMapMany(u -> repository.findByProfessionalIdAndStatusAndStartsAtBetween(u.getId(), AppointmentStatus.SCHEDULED, fromDt, toDt))
                .collectList()
                .map(list -> csvResponse(list, "agenda.csv"));
    }

    // Admin: export CSV of a professional's appointments
    @GetMapping("/api/admin/professionals/{userId}/appointments/export")
    @Operation(summary = "Exportar CSV citas de un profesional (ADMIN)")
    public Mono<ResponseEntity<String>> exportAppointmentsByProfessionalCsv(@PathVariable Long userId,
                                                                            @RequestParam("from") String from,
                                                                            @RequestParam("to") String to) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        return repository.findByProfessionalIdAndStatusAndStartsAtBetween(userId, AppointmentStatus.SCHEDULED, fromDt, toDt)
                .collectList()
                .map(list -> csvResponse(list, "agenda-" + userId + ".csv"));
    }
    @GetMapping("/api/patient/appointments")
    @Operation(summary = "Listar mis turnos SCHEDULED (PATIENT)")
    public Flux<AppointmentResponse> getMyAppointmentsAsPatient(
            Authentication auth,
            @RequestParam("from") String from,
            @RequestParam("to") String to
    ) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMapMany(u -> reactor.core.publisher.Flux.merge(
                                repository.findByPatientIdAndStatusAndStartsAtBetween(
                                        u.getId(), AppointmentStatus.SCHEDULED, fromDt, toDt
                                ),
                                // incluir turnos creados por admin asociados por email
                                repository.findByEmailAndStatusAndStartsAtBetween(
                                        u.getEmail(), AppointmentStatus.SCHEDULED, fromDt, toDt
                                )
                        )
                        .distinct(MedicalAppointment::getId)
                )
                .map(mapper::toResponse);
    }

    // Professional: crear cita directa para sí mismo
    @PostMapping(value = "/api/professional/appointments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear cita directa (PROFESSIONAL)")
    public Mono<AppointmentResponse> createDirectByPro(Authentication auth, @RequestBody DirectAppointmentRequest request) {
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMap(u -> service.createDirect(new DirectAppointmentRequest(
                        u.getId(), request.specialty(), request.startsAt(), request.endsAt(),
                        request.firstName(), request.lastName(), request.email(), request.phone(),
                        request.coverageType(), request.healthInsurance(), request.healthPlan(), request.affiliateNumber(),
                        request.subject(), request.message()
                )));
    }

    // Professional: actualizar cita propia
    @PatchMapping(value = "/api/professional/appointments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar cita propia (PROFESSIONAL)")
    public Mono<AppointmentResponse> updateByPro(Authentication auth, @PathVariable Long id, @RequestBody UpdateAppointmentRequest request) {
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMap(u -> repository.findById(id)
                        .filter(a -> u.getId().equals(a.getProfessionalId()))
                        .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)))
                        .then(service.updateAppointment(id, request))
                );
    }

    // Professional: cancelar cita propia
    @PatchMapping("/api/professional/appointments/{id}/cancel")
    @Operation(summary = "Cancelar cita propia (PROFESSIONAL)")
    public Mono<Void> cancelByPro(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMap(u -> repository.findById(id)
                        .filter(a -> u.getId().equals(a.getProfessionalId()))
                        .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)))
                        .doOnNext(a -> { a.setStatus(AppointmentStatus.CANCELLED); a.setUpdatedAt(java.time.LocalDateTime.now()); })
                        .flatMap(repository::save)
                        .then());
    }

    // Patient: cancelar cita propia
    @PatchMapping("/api/patient/appointments/{id}/cancel")
    @Operation(summary = "Cancelar mi cita (PATIENT)")
    public Mono<Void> cancelByPatient(Authentication auth, @PathVariable Long id) {
        String username = auth.getName();
        return users.findByUsername(username)
                .flatMap(u -> repository.findById(id)
                        .filter(a -> u.getId().equals(a.getPatientId()))
                        .switchIfEmpty(Mono.error(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN)))
                        .doOnNext(a -> { a.setStatus(AppointmentStatus.CANCELLED); a.setUpdatedAt(java.time.LocalDateTime.now()); })
                        .flatMap(repository::save)
                        .then());
    }
    private ResponseEntity<String> csvResponse(java.util.List<MedicalAppointment> list, String filename) {
        String csv = toCsv(list);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.valueOf("text/csv; charset=utf-8"))
                .body(csv);
    }

    private String toCsv(java.util.List<MedicalAppointment> list) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("start,end,patient_first,patient_last,email,phone,coverage,insurance,status\n");
        for (MedicalAppointment a : list) {
            String start = a.getStartsAt() != null ? a.getStartsAt().format(df) : "";
            String end = a.getEndsAt() != null ? a.getEndsAt().format(df) : "";
            sb.append(q(start)).append(',')
              .append(q(end)).append(',')
              .append(q(nz(a.getFirstName()))).append(',')
              .append(q(nz(a.getLastName()))).append(',')
              .append(q(nz(a.getEmail()))).append(',')
              .append(q(nz(a.getPhone()))).append(',')
              .append(q(a.getCoverageType() != null ? a.getCoverageType().name() : "")).append(',')
              .append(q(nz(a.getHealthInsurance()))).append(',')
              .append(q(a.getStatus() != null ? a.getStatus().name() : ""))
              .append('\n');
        }
        return sb.toString();
    }

    private static String q(String s) {
        String v = s == null ? "" : s.replace("\"", "\"\"");
        return '"' + v + '"';
    }

    private static String nz(String s) { return s == null ? "" : s; }

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
