package com.dinetime.identity_service.ports.output;

import com.dinetime.identity_service.domain.model.RefreshToken;

public interface RefreshTokenRepository {

    RefreshToken findByUserEmailAndDeviceIdAndToken(String userEmail, String deviceId, String token);
    void save(RefreshToken refreshToken);
    
}
