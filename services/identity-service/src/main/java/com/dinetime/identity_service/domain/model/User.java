package com.dinetime.identity_service.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private final UUID id;
    private final String hashedEmail;
    private final Instant createdAt;
    private List<Profile> profiles;

    public User(UUID id, String hashedEmail, Instant createdAt) {
        this.id= id;
        this.hashedEmail= hashedEmail;
        this.createdAt= createdAt;
        this.profiles = new ArrayList<>(); 
    }

    public static User createNew(String hashedEmail, Profile defaultProfile) {
        return new User(
            UUID.randomUUID(),
            hashedEmail,
            Instant.now(),
            List.of(defaultProfile)
        );
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public Profile findProfileById(UUID profileId) {
        return profiles.stream()
            .filter(p -> p.getId().equals(profileId))
            .findFirst()
            .orElse(null);
    }
}
