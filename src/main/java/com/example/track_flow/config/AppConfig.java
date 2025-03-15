package com.example.track_flow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 * Classe de configuração do Spring.
 * O Spring usa as classes anotadas com @Configuration para registrar beans na aplicação.
 */
@Configuration
public class AppConfig {

    /*
     * Cria e expõe um bean do tipo RestTemplate para que possa ser injetado em outros componentes.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}