package com.dinetime.ports.input;

public interface JwtTokenService {
    String generateGuestToken( String deviceId);

    String generateEmailVerificationToken(String email);
}
