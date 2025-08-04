package com.dinetime.identity_service.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dinetime.ports.input.JwtTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenServiceImpl(@Value("${security.jwt-secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(this.algorithm).withIssuer("dinetime").build();
    }

    public String generateGuestToken(String deviceId) {
        long now = System.currentTimeMillis();
        long expiry = now + 1800 * 1000;

        return JWT.create()
                .withSubject("guest")
                .withIssuer("dinetime")
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(expiry))
                .withClaim("device", deviceId)
                .withClaim("role", "guest")
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token); 
    }

    @Override
    public String generateEmailVerificationToken(String email) {
        return JWT.create()
        .withSubject(email)
        .withClaim("purpose", "email_verification")
        .withExpiresAt(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
        .sign(algorithm);
    }
}
