package cat.itacademy.s05.t02.n01.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cat.itacademy.s05.t02.n01.enums.Specialty;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("professional_profiles")
public class ProfessionalProfile {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("studies")
    private String studies;

    @Column("specialty")
    private Specialty specialty; 

    @Column("bio")
    private String bio;

    @Column("photo_url")
    private String photoUrl;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}

