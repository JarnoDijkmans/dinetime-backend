package com.dinetime.identity_service.adapters.web.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationResponse {
    private boolean success;
    private String message;
    private LocalDateTime expiresAt;
}