package com.dinetime.identity_service.adapters.persistence.jpa.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class EntityUser {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String hashedEmail;

    @Column(nullable = false)
    private Instant createdAt;

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EntityProfile> profiles = new ArrayList<>();

    public EntityUser(UUID id, String hashedEmail, Instant createdAt) {
        this.id = id;
        this.hashedEmail = hashedEmail;
        this.createdAt = createdAt;
    }
}
