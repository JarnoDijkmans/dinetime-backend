package com.dinetime.identity_service.ports.input;

public interface JwtTokenService {
    String generateGuestToken( String deviceId);
    String generateEmailVerificationToken(String email);
    String generateAccessToken(String email, String deviceId);
}
