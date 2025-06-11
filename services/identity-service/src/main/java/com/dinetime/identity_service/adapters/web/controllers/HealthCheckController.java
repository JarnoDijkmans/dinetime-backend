package com.dinetime.identity_service.adapters.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/identity/healthz")
    public String healthz() {
        return "OK";
    }
}