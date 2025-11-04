package com.dinetime.identity_service.adapters.persistence.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class CodeHasher {
    private static final String ALGORITHM = "HmacSHA256";
    private final SecretKeySpec keySpec;

    public CodeHasher(@Value("${security.code.pepper}") String pepper) {
        this.keySpec = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public String hash(String code) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(code.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to HMAC code", e);
        }
    }

    public boolean verify(String rawCode, String storedHash) {
        return MessageDigest.isEqual(
            hash(rawCode).getBytes(StandardCharsets.UTF_8),
            storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
