package com.watchdog;

import io.github.cdimascio.dotenv.Dotenv; // Correct Import for Dotenv
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableAsync // Enable asynchronous method execution
public class WatchdogApplication {

	public static void main(String[] args) {
		// Load .env file variables into system properties/environment.
		// This must happen BEFORE SpringApplication.run() to ensure properties are available during startup.
		try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> {
				// Set as system properties, which Spring Boot can then pick up
				System.setProperty(entry.getKey(), entry.getValue());
				// Optionally, you can also set them as actual environment variables,
				// but System.setProperty is usually sufficient for Spring Boot.
				// System.getenv().put(entry.getKey(), entry.getValue()); // Note: System.getenv() is usually immutable
			});
			System.out.println("Loaded .env file variables successfully.");
		} catch (RuntimeException e) { // Changed to catch RuntimeException for broader compatibility
			// DotenvException extends RuntimeException. Catching RuntimeException
			// ensures compilation even if DotenvException is not directly resolvable
			// in your specific setup, or if its package path changed.
			// This block will catch DotenvException if the .env file is not found.
			if (e.getClass().getName().equals("io.github.cdimascio.dotenv.DotenvException")) {
				System.out.println("No .env file found or could not be loaded. Relying on system environment variables or application.properties defaults. Details: " + e.getMessage());
			} else {
				System.err.println("An unexpected RuntimeException occurred while loading .env: " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("An unexpected error occurred while loading .env: " + e.getMessage());
			e.printStackTrace();
		}

		SpringApplication.run(WatchdogApplication.class, args);
	}

}
