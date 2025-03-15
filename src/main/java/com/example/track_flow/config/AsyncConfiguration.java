package com.example.track_flow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// Indica ao Spring que esta classe contém configurações de beans.
@Configuration
public class AsyncConfiguration {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Cria uma instância do ThreadPoolTaskExecutor, que é uma implementação
        // de Executor que encapsula um ThreadPoolExecutor.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Configura o número mínimo de threads que o pool vai manter ativas.
        executor.setCorePoolSize(10);
        
        // Configura o número máximo de threads permitidas no pool.
        executor.setMaxPoolSize(20);
        
        // Configura a capacidade da fila para segurar tarefas quando todas as threads estiverem ocupadas.
        executor.setQueueCapacity(100);
        
        // Define o prefixo para o nome das threads geradas por este executor.
        executor.setThreadNamePrefix("Async-Executor-");
        
        // Inicializa o executor, fazendo com que o ThreadPoolExecutor interno seja criado de acordo com
        // as configurações definidas (por exemplo, thread pool, fila, etc.).
        // Durante essa inicialização, se nenhum prefixo customizado foi definido via beanName,
        // o sistema pode ajustar o nome das threads.
        executor.initialize();
        
        // Retorna o executor configurado para ser usado na execução de tarefas assíncronas.
        return executor;
    }
}