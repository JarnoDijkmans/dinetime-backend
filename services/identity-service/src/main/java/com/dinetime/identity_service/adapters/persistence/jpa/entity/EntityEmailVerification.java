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

@Getter
@Table(
    name = "email_verifications",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EntityEmailVerification {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String code;

    private Instant expiresAt;

    public EntityEmailVerification(String email, String code, Instant expiresAt) {
        this.email = email;
        this.code = code;
        this.expiresAt = expiresAt;
    }
}
