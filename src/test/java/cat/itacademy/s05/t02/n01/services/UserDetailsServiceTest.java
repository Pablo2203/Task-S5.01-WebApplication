package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.model.User;
import cat.itacademy.s05.t02.n01.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsServiceTest {

    @Test
    void maps_user_to_userdetails_with_roles() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        org.springframework.security.crypto.password.PasswordEncoder encoder = Mockito.mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        UserDetailsService svc = new UserDetailsService(repo, encoder);

        User u = new User();
        u.setUsername("admin");
        u.setPasswordHash("hash");
        u.setRole("ADMIN");

        Mockito.when(repo.findByUsername("admin")).thenReturn(Mono.just(u));

        UserDetails details = svc.findByUsername("admin").block();
        assertNotNull(details);
        assertEquals("admin", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}

