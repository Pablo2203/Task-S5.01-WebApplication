package cat.itacademy.s05.t02.n01.config;

import cat.itacademy.s05.t02.n01.security.JwtSecurityContextRepository;
import cat.itacademy.s05.t02.n01.services.JwtService;
import cat.itacademy.s05.t02.n01.services.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;



@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(0)
    public SecurityWebFilterChain swaggerBasicChain(ServerHttpSecurity http,
                                                    PasswordEncoder passwordEncoder) {
// Usuario SÓLO para Swagger: swagger / Pablo2203
        var uds = new MapReactiveUserDetailsService(
                User.withUsername("swagger")
                        .password(passwordEncoder.encode("Pablo2203"))
                        .roles("SWAGGER")
                        .build()
        );
        var basicAuthManager = new UserDetailsRepositoryReactiveAuthenticationManager(uds);
        basicAuthManager.setPasswordEncoder(passwordEncoder);

        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml"
                ))
                .authenticationManager(basicAuthManager)      // usa el in-memory de arriba
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