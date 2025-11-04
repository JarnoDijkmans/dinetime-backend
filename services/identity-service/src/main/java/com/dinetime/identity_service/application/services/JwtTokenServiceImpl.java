package com.dinetime.identity_service.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dinetime.identity_service.ports.input.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    @Value("${security.access-token-ttl-minutes:15}")
    private long accessTokenTtlMinutes;

    public JwtTokenServiceImpl(@Value("${security.jwt-secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(this.algorithm)
                .withIssuer("dinetime")
                .build();
    }

    /**
     * Generates an access token for a given profile in a user's account.
     * @param email - The main account email.
     * @param deviceId - The device identifier.
     * @param profileId - The sub-profile ID (null or default if single-profile).
     */
    public String generateAccessToken(String email, String deviceId, UUID profileId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(email)
                .withIssuer("dinetime")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("device", deviceId)
                .withClaim("role", "user")
                .withClaim("profileId", profileId.toString())
                .sign(algorithm);
    }

    /**
     * Generates a guest token tied to a device and (optionally) a guest profile ID.
     */
    public String generateGuestToken(String deviceId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(15, ChronoUnit.MINUTES); 

        return JWT.create()
                .withSubject("guest")
                .withIssuer("dinetime")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withClaim("device", deviceId)
                .withClaim("role", "guest")
                .sign(algorithm);
    }

    /**
     * Validates and returns the decoded JWT.
     */
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    /**
     * Generates a short-lived token for email verification.
     */
    @Override
    public String generateEmailVerificationToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withClaim("purpose", "email_verification")
                .withIssuer("dinetime")
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }
}
