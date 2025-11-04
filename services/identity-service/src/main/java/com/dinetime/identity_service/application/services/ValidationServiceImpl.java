package com.dinetime.identity_service.application.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinetime.identity_service.adapters.persistence.security.EmailHasher;
import com.dinetime.identity_service.adapters.persistence.security.CodeHasher;
import com.dinetime.identity_service.adapters.web.request.EmailVerificationRequest;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.identity_service.domain.exceptions.VerificationException;
import com.dinetime.identity_service.domain.model.EmailAddress;
import com.dinetime.identity_service.domain.model.EmailVerification;
import com.dinetime.identity_service.ports.input.JwtTokenService;
import com.dinetime.identity_service.ports.input.ValidationService;
import com.dinetime.identity_service.ports.output.EmailSenderService;
import com.dinetime.identity_service.ports.output.UserRepository;
import com.dinetime.identity_service.ports.output.VerificationRepository;
import com.dinetime.identity_service.ports.input.RefreshTokenService;

@Service
public class ValidationServiceImpl implements ValidationService {
    private static final Duration CODE_TTL = Duration.ofMinutes(10);
    private static final int CODE_MIN = 100000;
    private static final int CODE_RANGE = 900000;

    private final JwtTokenService jwtService;
    private final EmailSenderService emailSenderService;
    private final VerificationRepository repository;
    private final EmailHasher emailHasher;
    private final CodeHasher codeHasher;
    private final SecureRandom secureRandom;
    private final Clock clock;
    private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);

    public ValidationServiceImpl(
            JwtTokenService jwtService,
            EmailSenderService emailSenderService,
            UserRepository userRepository,
            VerificationRepository repository,
            EmailHasher emailHasher,
            CodeHasher codeHasher,
            RefreshTokenService refreshTokenService,
            Clock clock 
    ) {
        this.jwtService = Objects.requireNonNull(jwtService);
        this.emailSenderService = Objects.requireNonNull(emailSenderService);
        this.repository = Objects.requireNonNull(repository);
        this.emailHasher = Objects.requireNonNull(emailHasher);
        this.codeHasher = Objects.requireNonNull(codeHasher);
        this.clock = (clock == null) ? Clock.systemUTC() : clock;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(EmailVerificationRequest request) {
        EmailAddress email = new EmailAddress(request.getEmail());

        // TODO: rate-limit by email and/or IP to avoid abuse
        String hashedEmail = emailHasher.hash(email);

        String code = generateVerificationCode();

        String hashedCode = codeHasher.hash(code);

        Instant expiresAt = Instant.now(clock).plus(CODE_TTL);

        EmailVerification verification = new EmailVerification(0L, hashedEmail, hashedCode, expiresAt, false);
        repository.save(verification);

        String token = jwtService.generateEmailVerificationToken(email.getValue());

        emailSenderService.sendVerificationEmail(email.getValue(), code, token);

        return new EmailVerificationResponse(true, "If this email exists, a verification message has been sent.",
                expiresAt);
    }

    private String generateVerificationCode() {
        int n = secureRandom.nextInt(CODE_RANGE);
        int code = CODE_MIN + n;
        return String.valueOf(code);
    }

   @Override
    public boolean validateEmailCode(String email, String code) {
        EmailVerification verification = verifyEmailCode(email, code);

        if (verification != null) {
            return true;
        }
        return false;
    }

    private EmailVerification verifyEmailCode(String email, String code) {
        EmailAddress emailAddress = new EmailAddress(email);
        String hashedEmail = emailHasher.hash(emailAddress);
        EmailVerification verification = repository.findByEmail(hashedEmail);

        if (verification == null || verification.getExpiresAt().isBefore(Instant.now(clock))) {
            log.warn("Verification failed: no record or expired for email {}", email);
            throw new VerificationException("Invalid verification attempt.");
        }

        String providedHash = codeHasher.hash(code);
        boolean matched = MessageDigest.isEqual(
                providedHash.getBytes(StandardCharsets.UTF_8),
                verification.getHashedCode().getBytes(StandardCharsets.UTF_8));

        if (!matched) {
            log.warn("Verification code mismatch for email {}", email);
            throw new VerificationException("Invalid verification attempt.");
        }
        verification.setVerified(true);
        repository.save(verification);
        return verification;
    }

}
