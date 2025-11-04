package com.dinetime.identity_service.adapters.persistence.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dinetime.identity_service.domain.model.EmailAddress;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class EmailHasher {

    private static final String HMAC_ALGO = "HmacSHA256";
    private final SecretKeySpec keySpec;

    public EmailHasher(@Value("${security.email.pepper}") String pepper) {
        this.keySpec = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
    }

    public String hash(EmailAddress email) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(email.getValue().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash email", e);
        }
    }

    public boolean verify(EmailAddress email, String storedHash) {
        String computedHash = hash(email);
        return MessageDigest.isEqual(
            computedHash.getBytes(StandardCharsets.UTF_8),
            storedHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}
