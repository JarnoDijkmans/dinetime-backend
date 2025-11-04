package com.dinetime.identity_service.adapters.persistence.jpa.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
    name = "email_verifications",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EntityEmailVerification {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String code;

    private Instant expiresAt;

    private boolean verified;

    public EntityEmailVerification(String email, String code, Instant expiresAt, boolean verified) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
        this.verified = verified;
    }
}
