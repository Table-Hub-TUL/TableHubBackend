package com.tablehub.thbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")      // optional, it's default
                .load();
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_TIME", dotenv.get("JWT_TIME"));
        System.setProperty("HEALTHCHECK_TOKEN", dotenv.get("HEALTHCHECK_TOKEN"));
        System.setProperty("JWT_REFRESH_TIME", dotenv.get("JWT_REFRESH_TIME"));
        SpringApplication.run(ThBackendApplication.class, args);
    }
}
