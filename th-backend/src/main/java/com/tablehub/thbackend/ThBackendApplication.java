package com.tablehub.thbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("th-backend/")        // current working directory
                .filename(".env")      // optional, it's default
                .load();
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_TIME", dotenv.get("JWT_TIME"));
        SpringApplication.run(ThBackendApplication.class, args);
    }
}
