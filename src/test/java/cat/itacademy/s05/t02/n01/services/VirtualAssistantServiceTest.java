package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.enums.Mood;
import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class VirtualAssistantServiceTest {

    @Test
    void interact_increases_level_and_clamps() {
        VirtualAssistantRepository repo = Mockito.mock(VirtualAssistantRepository.class);
        VirtualAssistantService svc = new VirtualAssistantService(repo);

        VirtualAssistant va = new VirtualAssistant();
        va.setId(1L);
        va.setInteractionLevel(5);
        va.setMood(Mood.GOODBYE);
        Mockito.when(repo.findAll()).thenReturn(Flux.just(va));
        Mockito.when(repo.save(Mockito.any(VirtualAssistant.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        VirtualAssistant updated = svc.interact().block();
        assertNotNull(updated);
        assertEquals(5, updated.getInteractionLevel()); // clamped at 5
        assertEquals(Mood.GOODBYE, updated.getMood());
    }

    @Test
    void updateMood_sets_mood_and_level() {
        VirtualAssistantRepository repo = Mockito.mock(VirtualAssistantRepository.class);
        VirtualAssistantService svc = new VirtualAssistantService(repo);

        VirtualAssistant va = new VirtualAssistant();
        va.setId(1L);
        va.setInteractionLevel(1);
        va.setMood(Mood.GREETING);
        Mockito.when(repo.findAll()).thenReturn(Flux.just(va));
        Mockito.when(repo.save(Mockito.any(VirtualAssistant.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        VirtualAssistant updated = svc.updateMood(Mood.CLAPPING).block();
        assertNotNull(updated);
        assertEquals(Mood.CLAPPING, updated.getMood());
        assertEquals(4, updated.getInteractionLevel());
    }
}
