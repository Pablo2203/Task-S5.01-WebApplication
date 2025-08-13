package cat.itacademy.s05.t02.n01.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {

    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("password_hash")
    private String passwordHash;

    @Column("role")
    private String role;

    @Column("enabled")
    private boolean enabled;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Version
    private Long version;
}