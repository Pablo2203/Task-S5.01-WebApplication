
package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.enums.Mood;
import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import cat.itacademy.s05.t02.n01.services.VirtualAssistantService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/virtual-assistant")
public class VirtualAssistantController {

    private final VirtualAssistantService assistantService;

    public VirtualAssistantController(VirtualAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    // Obtener el estado general de la asistente virtual
    @GetMapping
    public Mono<VirtualAssistant> getAssistant() {
        return assistantService.getAssistant();
    }

    // Incrementar el nivel de interacción (ejemplo: por acción del usuario)
    @PostMapping("/interact")
    public Mono<VirtualAssistant> interact() {
        return assistantService.interact();

    }

    // Cambiar el estado general (ejemplo: administradores)
    @PostMapping("/update-status")
    public Mono<VirtualAssistant> updateStatus(@RequestBody Mood estado) {
        return assistantService.updateMood(estado);

    }
}
