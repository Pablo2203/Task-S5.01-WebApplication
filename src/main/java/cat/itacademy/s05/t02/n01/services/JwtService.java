package cat.itacademy.s05.t02.n01.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-minutes}") long expiration) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.expirationMinutes = expiration;
    }


    public String generateToken(String username, Collection<String> roles) {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("roles", roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .build();

        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(new MACSigner(secretKey));  // requiere clave ≥ 256 bits
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Error al firmar el JWT", e);
        }
    }

    public JWTClaimsSet validate(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new MACVerifier(secretKey))) {
                throw new BadCredentialsException("Firma JWT inválida");
            }
            if (jwt.getJWTClaimsSet().getExpirationTime().before(new Date())) {
                throw new BadCredentialsException("Token expirado");
            }
            return jwt.getJWTClaimsSet();
        } catch (JOSEException | ParseException e) {
            throw new BadCredentialsException("Token inválido", e);
        }
    }
}


//openssl rand -hex 32 comando para generar claves random