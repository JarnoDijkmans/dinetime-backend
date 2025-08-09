package com.dinetime.identity_service.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinetime.identity_service.adapters.web.request.EmailValidateRequest;
import com.dinetime.identity_service.adapters.web.request.EmailVerificationRequest;
import com.dinetime.identity_service.adapters.web.response.AuthResponse;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.identity_service.ports.input.ValidationService;

@RestController
@RequestMapping("/api/token")
public class ValidationController {
    private final ValidationService validateService;

    public ValidationController(ValidationService validateService) {
        this.validateService = validateService;
    }

    @PostMapping("/email-validation/request")
    public ResponseEntity<Object> sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty email");
        }

        try {
            EmailVerificationResponse response = validateService.sendEmailVerificationCode(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/email-validation/validate")
    public ResponseEntity<Object> validateEmailCode(@RequestBody EmailValidateRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty email");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty code");
        }

        try {
            AuthResponse response = validateService.validateEmailCode(request.getEmail(), request.getCode(), request.getDeviceId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while validating the email code");
        }
    }

}
