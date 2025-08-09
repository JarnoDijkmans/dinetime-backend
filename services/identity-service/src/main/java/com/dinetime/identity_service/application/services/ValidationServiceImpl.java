package com.dinetime.identity_service.application.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.dinetime.identity_service.adapters.persistence.security.EmailHasher;
import com.dinetime.identity_service.adapters.persistence.security.CodeHasher;
import com.dinetime.identity_service.adapters.web.response.AuthResponse;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.identity_service.domain.model.EmailAddress;
import com.dinetime.identity_service.domain.model.EmailVerification;
import com.dinetime.identity_service.ports.input.JwtTokenService;
import com.dinetime.identity_service.ports.input.ValidationService;
import com.dinetime.identity_service.ports.output.EmailSenderService;
import com.dinetime.identity_service.ports.output.VerificationRepository;
import com.dinetime.identity_service.ports.input.RefreshTokenService;

@Service
public class ValidationServiceImpl implements ValidationService {
    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final int CODE_MIN = 100000; // 6-digit
    private static final int CODE_RANGE = 900000;

    private final JwtTokenService jwtService;
    private final EmailSenderService emailSenderService;
    private final VerificationRepository repository;
    private final EmailHasher emailHasher;
    private final CodeHasher codeHasher;
    private final RefreshTokenService refreshTokenService;
    private final SecureRandom secureRandom;
    private final Clock clock;

    public ValidationServiceImpl(
            JwtTokenService jwtService,
            EmailSenderService emailSenderService,
            VerificationRepository repository,
            EmailHasher emailHasher,
            CodeHasher codeHasher,
            RefreshTokenService refreshTokenService,
            Clock clock // inject Clock.systemUTC() in production
    ) {
        this.jwtService = Objects.requireNonNull(jwtService);
        this.emailSenderService = Objects.requireNonNull(emailSenderService);
        this.repository = Objects.requireNonNull(repository);
        this.emailHasher = Objects.requireNonNull(emailHasher);
        this.codeHasher = Objects.requireNonNull(codeHasher);
        this.refreshTokenService = Objects.requireNonNull(refreshTokenService);
        this.clock = (clock == null) ? Clock.systemUTC() : clock;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(String requestEmail) {
        EmailAddress email = new EmailAddress(requestEmail); // ensure constructor validates format

        // TODO: rate-limit by email and/or IP to avoid abuse
        String hashedEmail = emailHasher.hash(email); // deterministic hash used for lookup

        // generate numeric code
        String code = generateVerificationCode();

        // hash the code before saving (use a keyed HMAC or bcrypt in CodeHasher implementation)
        String hashedCode = codeHasher.hash(code);

        Instant expiresAt = Instant.now(clock).plus(CODE_TTL);

        EmailVerification verification = new EmailVerification(hashedEmail, hashedCode, expiresAt);
        repository.save(verification);

        // Generate an email verification token if you want to support link-click verification
        String token = jwtService.generateEmailVerificationToken(email.getValue());

        // send the email with the code (and optionally verification link)
        emailSenderService.sendVerificationEmail(email.getValue(), code, token);

        return new EmailVerificationResponse(true, "If this email exists, a verification message has been sent.", expiresAt);
    }

    private String generateVerificationCode() {
        int n = secureRandom.nextInt(CODE_RANGE); 
        int code = CODE_MIN + n; 
        return String.valueOf(code);
    }

    @Override
    public AuthResponse validateEmailCode(String email, String code, String deviceId) {
        EmailAddress emailAddress = new EmailAddress(email);
        String hashedEmail = emailHasher.hash(emailAddress);
        EmailVerification verification = repository.findByEmail(hashedEmail);

    if (verification == null) {
        // TODO: count failed attempt for rate-limiting & logging
        throw new IllegalArgumentException("Invalid verification attempt.");
    }

    Instant now = Instant.now(clock);
    if (verification.getExpiresAt().isBefore(now)) {
        repository.delete(verification);
        throw new IllegalArgumentException("Invalid verification attempt.");
    }

    String providedHash = codeHasher.hash(code);
    boolean matched = MessageDigest.isEqual(
        providedHash.getBytes(StandardCharsets.UTF_8),
        verification.getHashedCode().getBytes(StandardCharsets.UTF_8)
    );

    if (!matched) {
        // TODO: increment attempt counter and apply exponential backoff / lockout if too many tries
        throw new IllegalArgumentException("Invalid verification attempt.");
    }

    repository.delete(verification);

    // Generate tokens
    String accessToken = jwtService.generateAccessToken(emailAddress.getValue(), deviceId);
    String refreshToken = refreshTokenService.generateAndStoreRefreshToken(emailAddress.getValue(), deviceId);

    return new AuthResponse(accessToken, refreshToken);
}

}
