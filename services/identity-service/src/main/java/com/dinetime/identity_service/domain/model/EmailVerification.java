package com.dinetime.identity_service.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {
    private Long id;
    private String hashedEmail;
    private String hashedCode;
    private Instant expiresAt;
    @Setter
    private boolean verified;
}
