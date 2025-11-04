package com.dinetime.identity_service.ports.input;

public interface RefreshTokenService {
     /**
     * Generate a new refresh token for the given user + device,
     * store it securely, and return the token string.
     */
    String generateAndStoreRefreshToken(String userEmail, String deviceId);

    /**
     * Validate the given refresh token for the user + device.
     * Returns true if valid and not expired/revoked.
     */
    boolean validateRefreshToken(String userEmail, String deviceId, String refreshToken);

    /**
     * Revoke the refresh token (logout).
     */
    void revokeRefreshToken(String userEmail, String deviceId, String refreshToken);
}
