package com.dinetime.matchmaker.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/matchmaker")
    public ResponseEntity<String> root_matchmaker() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/matchmaker/healthz")
    public ResponseEntity<String> healthCheckMatchMaker() {
        return ResponseEntity.ok("OK");
    }
}
