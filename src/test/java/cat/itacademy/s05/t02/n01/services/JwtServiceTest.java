package cat.itacademy.s05.t02.n01.services;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generate_and_validate_ok() {
        JwtService svc = new JwtService("12345678901234567890123456789012", 60);
        String token = svc.generateToken("alice", List.of("ADMIN", "USER"));
        JWTClaimsSet claims = svc.validate(token);
        assertEquals("alice", claims.getSubject());
        @SuppressWarnings("unchecked")
        var roles = (java.util.List<String>) claims.getClaim("roles");
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
    }

    @Test
    void invalid_signature_rejected() {
        JwtService good = new JwtService("12345678901234567890123456789012", 60);
        JwtService bad = new JwtService("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 60);
        String token = good.generateToken("bob", List.of("PATIENT"));
        assertThrows(BadCredentialsException.class, () -> bad.validate(token));
    }

    @Test
    void expired_token_rejected() {
        JwtService svc = new JwtService("12345678901234567890123456789012", -1); // ya expirado
        String token = svc.generateToken("carol", List.of("USER"));
        assertThrows(BadCredentialsException.class, () -> svc.validate(token));
    }
}

