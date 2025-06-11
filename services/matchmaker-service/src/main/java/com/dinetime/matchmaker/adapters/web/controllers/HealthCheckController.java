package com.dinetime.matchmaker.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matchmaker")
public class HealthCheckController {
    
    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping({"", "/"})
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("OK");
    }
}