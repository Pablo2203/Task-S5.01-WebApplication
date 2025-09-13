package cat.itacademy.s05.t02.n01.config;

import cat.itacademy.s05.t02.n01.security.JwtSecurityContextRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.WebFilter;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(0)
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            ReactiveAuthenticationManager authenticationManager) {

        return http
                .securityContextRepository(new JwtSecurityContextRepository(jwtService, userDetailsService))
                .authenticationManager(authenticationManager)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                        .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/webjars/**",
                                "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .pathMatchers("/api/appointments/requests").permitAll()
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/professional/**").hasAnyRole("PROFESSIONAL", "ADMIN")
                        .pathMatchers("/api/patient/**").hasAnyRole("PATIENT", "ADMIN")
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }

    // Elimina cualquier cabecera WWW-Authenticate para evitar prompt Basic del navegador
    @Bean
    public WebFilter suppressWwwAuthenticateHeader() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> {
                exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
                return Mono.empty();
            });
            return chain.filter(exchange);
        };
    }
}
