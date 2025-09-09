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
import org.springframework.security.config.Customizer;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;


@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(0)
    public SecurityWebFilterChain swaggerHttpBasic(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.matchers(
                        new PathPatternParserServerWebExchangeMatcher("/swagger-ui.html"),
                        new PathPatternParserServerWebExchangeMatcher("/swagger-ui/"),
                        new PathPatternParserServerWebExchangeMatcher("/webjars/"),
                        new PathPatternParserServerWebExchangeMatcher("/v3/api-docs"),
                        new PathPatternParserServerWebExchangeMatcher("/v3/api-docs/**"),
                        new PathPatternParserServerWebExchangeMatcher("/v3/api-docs.yaml")
                ))
                .authorizeExchange(ex -> ex.anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            ReactiveAuthenticationManager authenticationManager) {
        return http
                .securityContextRepository(new JwtSecurityContextRepository(jwtService, userDetailsService))
                .authenticationManager(authenticationManager)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/appointments/requests").permitAll()
                        .pathMatchers("/api/admin/").hasRole("ADMIN")
                        .pathMatchers("/api/professional/").hasAnyRole("PROFESSIONAL", "ADMIN")
                        .pathMatchers("/api/patient/**").hasAnyRole("PATIENT", "ADMIN")
                        .anyExchange().authenticated())
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
}
