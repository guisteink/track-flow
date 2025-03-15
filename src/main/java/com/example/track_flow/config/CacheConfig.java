package com.example.track_flow.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    // Define um bean do tipo CacheManager para gerenciar os caches na aplicação.
    @Bean
    public CacheManager cacheManager() {
        // Cria um gerenciador de cache utilizando Caffeine, configurado para usar um cache chamado "packages".
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("packages");
        // Define as configurações do Caffeine (tempo de expiração e tamanho máximo do cache).
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    // Configura o comportamento do cache utilizando Caffeine.
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                // Configura o cache para expirar 60 segundos após uma escrita.
                .expireAfterWrite(60, TimeUnit.SECONDS)
                // Define o tamanho máximo do cache como 1000 entradas.
                .maximumSize(1000);
    }
}