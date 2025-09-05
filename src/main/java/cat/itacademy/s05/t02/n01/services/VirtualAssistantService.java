package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.enums.Mood;
import cat.itacademy.s05.t02.n01.model.VirtualAssistant;
import cat.itacademy.s05.t02.n01.repositories.VirtualAssistantRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service layer responsible for encapsulating the business rules related to the
 * {@link VirtualAssistant} entity.
 *
 * <p>Rules:
 * <ul>
 *   <li><strong>interactionLevel</strong> is always kept between 1 and 5.</li>
 *   <li><strong>mood</strong> is derived from the interaction level or can be
 *   overridden explicitly.</li>
 *   <li><strong>updatedAt</strong> is refreshed on every change.</li>
 * </ul>
 * </p>
 */
@Service
public class VirtualAssistantService {

    private final VirtualAssistantRepository assistantRepository;

    public VirtualAssistantService(VirtualAssistantRepository assistantRepository) {
        this.assistantRepository = assistantRepository;
    }

    /**
     * Retrieves the single instance of the virtual assistant stored in the
     * database.  The application is designed to work with a single assistant so
     * we simply return the first element.
     */
    public Mono<VirtualAssistant> getAssistant() {
        return assistantRepository.findAll().next();
    }

    /**
     * Increases the interaction level of the assistant by one unit. The value
     * is clamped to the [1,5] range. After updating the interaction level the
     * mood is recalculated and the {@code updatedAt} timestamp is refreshed.
     */
    public Mono<VirtualAssistant> interact() {
        return getAssistant().flatMap(assistant -> {
            int newLevel = clampInteractionLevel(assistant.getInteractionLevel() + 1);
            assistant.setInteractionLevel(newLevel);
            assistant.setMood(calculateMood(newLevel));
            assistant.setUpdatedAt(LocalDateTime.now());
            return assistantRepository.save(assistant);
        });
    }

    /**
     * Updates the mood of the assistant explicitly. The timestamp is refreshed
     * to reflect the modification.
     *
     * @param mood new mood
     */
    public Mono<VirtualAssistant> updateMood(Mood mood) {
        return getAssistant().flatMap(assistant -> {
            assistant.setMood(mood);
            assistant.setInteractionLevel(moodToInteractionLevel(mood));
            assistant.setUpdatedAt(LocalDateTime.now());
            return assistantRepository.save(assistant);
        });
    }

    // ------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------

    /**
     * Ensures that the provided interaction level falls within the 1-5 range.
     */
    private int clampInteractionLevel(int level) {
        if (level < 1) {
            return 1;
        } else if (level > 5) {
            return 5;
        }
        return level;
    }

    /**
     * Derives the assistant's mood directly from its interaction level.
     */
    private Mood calculateMood(int interactionLevel) {
        switch (interactionLevel) {
            case 1:
                return Mood.GREETING;
            case 2:
                return Mood.NEUTRAL;
            case 3:
                return Mood.THOUGHTFUL;
            case 4:
                return Mood.CLAPPING;
            case 5:
                return Mood.GOODBYE;
            default:
                return Mood.GREETING;
        }
    }

    /**
     * Maps a mood to its corresponding interaction level.
     */
    private int moodToInteractionLevel(Mood mood) {
        switch (mood) {
            case GREETING:
                return 1;
            case NEUTRAL:
                return 2;
            case THOUGHTFUL:
                return 3;
            case CLAPPING:
                return 4;
            case GOODBYE:
                return 5;
            default:
                return 1;
        }
    }
}

