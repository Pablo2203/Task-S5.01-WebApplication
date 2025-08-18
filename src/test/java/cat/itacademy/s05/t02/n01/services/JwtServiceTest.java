package cat.itacademy.s05.t02.n01.services;

import cat.itacademy.s05.t02.n01.services.JwtService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET_1 = "01234567890123456789012345678901"; // 32 bytes
    private static final String SECRET_2 = "abcdef0123456789abcdef0123456789"; // 32 bytes

    @Test
    void generateTokenAndValidate() throws Exception {
        JwtService service = new JwtService(SECRET_1, 60);

        String token = service.generateToken("alice", List.of("ROLE_USER", "ROLE_ADMIN"));

        SignedJWT parsed = SignedJWT.parse(token);
        JWTClaimsSet claims = parsed.getJWTClaimsSet();
        assertEquals("alice", claims.getSubject());
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), claims.getStringListClaim("roles"));

        JWTClaimsSet validated = service.validate(token);
        assertEquals("alice", validated.getSubject());
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), validated.getStringListClaim("roles"));
    }

    @Test
    void validateThrowsOnInvalidSignature() {
        JwtService signer = new JwtService(SECRET_1, 60);
        JwtService validator = new JwtService(SECRET_2, 60);

        String token = signer.generateToken("bob", List.of("ROLE_USER"));

        assertThrows(BadCredentialsException.class, () -> validator.validate(token));
    }

    @Test
    void validateThrowsOnExpiredToken() {
        JwtService service = new JwtService(SECRET_1, -1); // token already expired
        String token = service.generateToken("carol", List.of("ROLE_USER"));

        assertThrows(BadCredentialsException.class, () -> service.validate(token));
    }
}