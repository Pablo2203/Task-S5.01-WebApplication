package cat.itacademy.s05.t02.n01.assistant;

import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DataLoader {

    private final VirtualAssistantRepository assistantRepository;

    public DataLoader(VirtualAssistantRepository assistantRepository) {
        this.assistantRepository = assistantRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialData() {
        assistantRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> Flux.just(
                        new VirtualAssistant(null, "Cecilia", "Neutral", 50)
                ))
                .flatMap(assistantRepository::save)
                .subscribe();
    }
}