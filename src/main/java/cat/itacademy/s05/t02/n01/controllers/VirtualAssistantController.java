package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/virtual-assistant")
public class VirtualAssistantController {

    private final VirtualAssistantRepository assistantRepository;

    public VirtualAssistantController(VirtualAssistantRepository assistantRepository) {
        this.assistantRepository = assistantRepository;
    }

    // Obtener el estado general de la asistente virtual
    @GetMapping
    public Mono<VirtualAssistant> getAssistant() {
        return assistantRepository.findAll().next(); // Obtiene la primera instancia
    }

    // Incrementar el nivel de interacción (ejemplo: por acción del usuario)
    @PostMapping("/interact")
    public Mono<VirtualAssistant> interact() {
        return assistantRepository.findAll()
                .next()
                .flatMap(assistant -> {
                    assistant.setInteractionLevel(assistant.getInteractionLevel() + 1);
                    return assistantRepository.save(assistant); // Guarda los cambios
                });
    }

    // Cambiar el estado general (ejemplo: administradores)
    @PostMapping("/update-status")
    public Mono<VirtualAssistant> updateStatus(@RequestBody String estado) {
        return assistantRepository.findAll()
                .next()
                .flatMap(assistant -> {
                    assistant.setState(estado);
                    return assistantRepository.save(assistant); // Guarda los cambios
                });
    }
}