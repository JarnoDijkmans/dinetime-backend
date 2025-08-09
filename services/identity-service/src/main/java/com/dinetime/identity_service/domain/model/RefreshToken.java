package com.dinetime.identity_service.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    private String userEmail;
    private String deviceId;
    private String token;

    private Instant expiresAt; 

    @Setter
    private Instant lastUsedAt;

    @Setter
    private boolean revoked;

    public RefreshToken (String userEmail, String deviceId, String token, Instant expiresAt, boolean revoked) {
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }
}
