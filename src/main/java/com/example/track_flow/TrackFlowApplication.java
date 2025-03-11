package com.example.track_flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.example.track_flow")
public class TrackFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackFlowApplication.class, args);
	}

}
