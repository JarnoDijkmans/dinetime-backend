package com.dinetime.identity_service.ports.input;

import java.util.UUID;

public interface JwtTokenService {
    String generateGuestToken( String deviceId);
    String generateEmailVerificationToken(String email);
    String generateAccessToken(String email, String deviceId, UUID profileId);
}
