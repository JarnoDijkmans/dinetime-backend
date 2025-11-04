package com.dinetime.identity_service.adapters.persistence.jpa.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "refresh_tokens")
@Getter
@AllArgsConstructor
public class EntityRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String deviceId;
    private String token;

    private Instant expiresAt; 

    private Instant lastUsedAt;

    private boolean revoked;

    public EntityRefreshToken(String userEmail, String deviceId, String token, Instant expiresAt, Instant lastUserAt, boolean revoked) {
        this.userEmail = userEmail;
        this.deviceId = deviceId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUserAt;
        this.revoked = revoked;
    }

}
