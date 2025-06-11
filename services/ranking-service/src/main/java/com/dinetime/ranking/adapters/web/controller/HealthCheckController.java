package com.dinetime.ranking.adapters.web.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ranking")
    public ResponseEntity<String> root_matchmaker() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/ranking/healthz")
    public ResponseEntity<String> healthCheckMatchMaker() {
        return ResponseEntity.ok("OK");
    }
}
