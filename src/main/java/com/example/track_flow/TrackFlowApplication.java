package com.example.track_flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

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
