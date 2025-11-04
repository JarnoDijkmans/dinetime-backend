package com.dinetime.identity_service.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Profile {

    private final UUID id;
    private final UUID userId;
    private String displayName;
    private final Instant createdAt;
    private Instant updatedAt;

    public static Profile createDefault(UUID userId) {
        Instant now = Instant.now();
        return new Profile(UUID.randomUUID(), userId, "Default", now, now);
    }

    public void rename(String newDisplayName) {
        this.displayName = newDisplayName;
        this.updatedAt = Instant.now();
    }
}
