package com.tablehub.thbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {

    @Value("${HEALTHCHECK_TOKEN}")
    private static String healthcheckToken;

    @GetMapping("")
    public ResponseEntity<String> healthCheck(@RequestParam String token) {
        if (healthcheckToken.equals(token)) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(403).body("Forbidden");
        }
    }
}
