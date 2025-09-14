package com.swiftlogistics.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class OrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorApplication.class, args);
		System.out.println("Orchestrator Service is running...");
	}

}
