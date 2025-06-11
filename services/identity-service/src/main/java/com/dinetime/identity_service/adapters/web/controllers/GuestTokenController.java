package com.dinetime.identity_service.adapters.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dinetime.identity_service.adapters.web.request.GuestTokenRequest;
import com.dinetime.identity_service.adapters.web.response.GuestTokenResponse;
import com.dinetime.identity_service.application.services.JwtTokenService;

@RestController
@RequestMapping("/identity/api/token")
public class GuestTokenController {

    private final JwtTokenService jwtTokenService;

    public GuestTokenController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/guest")
    public ResponseEntity<?> generateGuestToken(@RequestBody GuestTokenRequest request) {
        if (request.getDeviceId() == null) {
            return ResponseEntity.badRequest().body("Missing deviceId");
        }
        String token = jwtTokenService.generateGuestToken(request.getDeviceId());
        return ResponseEntity.ok(new GuestTokenResponse(token));
    }

    @GetMapping("/guest/validate")
    public ResponseEntity<?> validateGuestToken(@RequestHeader String token) {
        try {
            jwtTokenService.validateToken(token);
            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }
    }
}
