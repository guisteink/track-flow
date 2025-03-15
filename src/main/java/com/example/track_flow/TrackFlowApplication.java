package com.example.track_flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal da aplicação TrackFlow.
 *
 * <p>Configura e inicializa a aplicação Spring Boot com os recursos necessários.
 *
 * @SpringBootApplication - Ativa a configuração automática, a inicialização do Spring Boot e funcionalidades gerais.
 * @EntityScan - Define o pacote onde as entidades JPA estão localizadas.
 * @EnableJpaRepositories - Especifica o pacote que contém os repositórios JPA.
 * @ComponentScan - Realiza a varredura de componentes nos pacotes indicados.
 * @EnableCaching - Habilita o suporte a cache na aplicação.
 * @EnableAsync - Permite a execução assíncrona de métodos.
 *
 * O método main é o ponto de entrada que inicia o contexto Spring.
 */
@SpringBootApplication
@EntityScan("com.example.track_flow.model")
@EnableJpaRepositories("com.example.track_flow.repository")
@ComponentScan("com.example.track_flow")
@EnableCaching
@EnableAsync
public class TrackFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackFlowApplication.class, args);
    }
}
