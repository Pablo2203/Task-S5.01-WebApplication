package cat.itacademy.s05.t02.n01.security;

import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.UserDetailsService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtSecurityContextRepository(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                JWTClaimsSet claims = jwtService.validate(token);
                String username = claims.getSubject();
                List<String> roles = (List<String>) claims.getClaim("roles");
                Collection<SimpleGrantedAuthority> authorities = roles == null ? List.of() :
                        roles.stream()
                                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                return userDetailsService.findByUsername(username)
                        .map(user -> new UsernamePasswordAuthenticationToken(user.getUsername(), token, authorities))
                        .map(SecurityContextImpl::new);
            } catch (Exception e) {
                return Mono.empty();
            }
        }
        return Mono.empty();
    }
}