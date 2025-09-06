package cat.itacademy.s05.t02.n01.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "YRIGOYEN API",
                version = "1.0",
                description = "Endpoints públicos y privados para turnos manuales, perfiles y recordatorios",
                contact = @Contact(name = "YRIGOYEN")
        )
)
public class OpenApiConfig {}

