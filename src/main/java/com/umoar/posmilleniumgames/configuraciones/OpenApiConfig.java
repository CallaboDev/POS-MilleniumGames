package com.umoar.posmilleniumgames.configuraciones;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI posOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("POS Millenium Games - API REST")
                        .version("1.0.0")
                        .description("Documentación interactiva del sistema de punto de venta (POS) para Millenium Games, visualizada mediante Scalar.")
                        .contact(new Contact()
                                .name("Millenium Games")
                                .email("soporte@milleniumgames.com"))
                        .license(new License()
                                .name("Privado - Uso Interno")))
                .servers(List.of(
                        new Server().url("/").description("Servidor Base")
                ));
    }
}
