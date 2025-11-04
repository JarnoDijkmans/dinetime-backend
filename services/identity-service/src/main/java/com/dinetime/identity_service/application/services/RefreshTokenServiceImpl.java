package com.dinetime.identity_service.application.services;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.dinetime.identity_service.domain.model.RefreshToken;
import com.dinetime.identity_service.ports.input.RefreshTokenService;
import com.dinetime.identity_service.ports.output.RefreshTokenRepository;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration refreshTokenTTL = Duration.ofDays(30); 

    public RefreshTokenServiceImpl(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateAndStoreRefreshToken(String userEmail, String deviceId) {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        Instant expiresAt = Instant.now().plus(refreshTokenTTL);

        RefreshToken refreshToken = new RefreshToken(userEmail, deviceId, token, expiresAt, false);

        repository.save(refreshToken);

        return token;
    }

    @Override
    public boolean validateRefreshToken(String userEmail, String deviceId, String token) {
        RefreshToken rt = repository.findByUserEmailAndDeviceIdAndToken(userEmail, deviceId, token);

        if (rt == null || rt.isRevoked()) {
            return false;
        }

        Instant now = Instant.now();
        if (rt.getExpiresAt() != null && rt.getExpiresAt().isBefore(now)) {
            return false;
        }

        if (rt.getLastUsedAt() != null && rt.getLastUsedAt().plus(Duration.ofDays(90)).isBefore(now)) {
            rt.setRevoked(true);
            repository.save(rt);
            return false;
        }

        rt.setLastUsedAt(now);
        repository.save(rt);

        return true;
    }


    @Override
    public void revokeRefreshToken(String userEmail, String deviceId, String refreshToken) {
        RefreshToken storedToken = repository.findByUserEmailAndDeviceIdAndToken(userEmail, deviceId, refreshToken);
        if (storedToken != null) {
            storedToken.setRevoked(true);
            repository.save(storedToken);
        }
    }
}
