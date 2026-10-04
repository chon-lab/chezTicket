package com.chezticket.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Sobe um MariaDB real em container para os testes de integração.
 * {@code @ServiceConnection} injeta a URL/usuário/senha no contexto Spring
 * automaticamente — não é preciso configurar datasource no teste.
 *
 * <p>Requer Docker em execução na máquina.
 */
@TestConfiguration(proxyBeanMethods = false)
public class MariaDbTestContainer {

    @Bean
    @ServiceConnection
    MariaDBContainer<?> mariaDbContainer() {
        return new MariaDBContainer<>(DockerImageName.parse("mariadb:11.4"));
    }
}
