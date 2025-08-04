package com.dinetime.identity_service.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinetime.identity_service.adapters.web.request.EmailVerificationRequest;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.ports.input.UserService;

@RestController
@RequestMapping("/api/token")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/email-validation/request")
    public ResponseEntity<Object> sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty email");
        }

        try {
            EmailVerificationResponse response = userService.sendEmailVerificationCode(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
