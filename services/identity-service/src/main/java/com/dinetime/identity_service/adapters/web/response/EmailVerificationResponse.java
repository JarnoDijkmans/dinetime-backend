package com.dinetime.identity_service.adapters.web.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationResponse {
    private boolean success;
    private String message;
    private Instant expiresAt;
}