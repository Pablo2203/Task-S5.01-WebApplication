package cat.itacademy.s05.t02.n01.model;

import cat.itacademy.s05.t02.n01.enums.Mood;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("virtual_assistants")
public class VirtualAssistant {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("mood")
    private Mood mood;

    @Column("interaction_level")
    private Integer interactionLevel;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}