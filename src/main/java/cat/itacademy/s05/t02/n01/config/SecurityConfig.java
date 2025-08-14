package cat.itacademy.s05.t02.n01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        // Endpoints públicos
                        .pathMatchers("/auth/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // Solo ADMIN
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")

                        // Profesionales y Admin
                        .pathMatchers("/api/professional/**").hasAnyRole("PROFESSIONAL", "ADMIN")

                        // Pacientes y Admin
                        .pathMatchers("/api/patient/**").hasAnyRole("PATIENT", "ADMIN")

                        // Cualquier otro endpoint requiere estar autenticado
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}