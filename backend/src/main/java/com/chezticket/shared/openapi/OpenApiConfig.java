package com.chezticket.shared.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI chezTicketOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("chezTicket API")
                        .version("0.1.0")
                        .description("""
                                API da plataforma de ingressos chezTicket (CEFET/RJ).

                                Arquitetura hexagonal · Java 17 · Spring Boot · MariaDB · Flyway.
                                Os erros seguem o formato RFC 7807 (application/problem+json).
                                """)
                        .contact(new Contact()
                                .name("Equipe chezTicket")
                                .url("https://github.com/chon-lab/chezTicket"))
                        .license(new License().name("Uso acadêmico — CEFET/RJ")))
                .servers(List.of(new Server().url("/").description("Servidor atual")));
    }
}
