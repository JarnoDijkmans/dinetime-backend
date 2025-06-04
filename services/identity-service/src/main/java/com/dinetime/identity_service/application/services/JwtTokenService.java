package com.dinetime.identity_service.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenService(@Value("${security.jwt-secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        this.verifier = JWT.require(this.algorithm).withIssuer("dinetimelobby").build();
    }

    public String generateGuestToken(String deviceId) {
        long now = System.currentTimeMillis();
        long expiry = now + 1800 * 1000;

        return JWT.create()
                .withSubject("guest")
                .withIssuer("dinetimelobby")
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(expiry))
                .withClaim("device", deviceId)
                .withClaim("role", "guest")
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token); 
    }
}
