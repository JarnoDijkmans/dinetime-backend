package com.dinetime.identity_service.application.services;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dinetime.identity_service.adapters.persistence.security.EmailHasher;
import com.dinetime.identity_service.adapters.web.request.CreateUserProfileRequest;
import com.dinetime.identity_service.adapters.web.response.AuthResponse;
import com.dinetime.identity_service.domain.model.EmailAddress;
import com.dinetime.identity_service.domain.model.EmailVerification;
import com.dinetime.identity_service.domain.model.Profile;
import com.dinetime.identity_service.domain.model.User;
import com.dinetime.identity_service.ports.input.JwtTokenService;
import com.dinetime.identity_service.ports.input.RefreshTokenService;
import com.dinetime.identity_service.ports.input.UserService;
import com.dinetime.identity_service.ports.output.EmailSenderService;
import com.dinetime.identity_service.ports.output.UserRepository;
import com.dinetime.identity_service.ports.output.VerificationRepository;

@Service
public class UserServiceImpl implements UserService {

    private final JwtTokenService jwtService;
    private final UserRepository userRepository;
    private final VerificationRepository repository;
    private final EmailHasher emailHasher;
    private final RefreshTokenService refreshTokenService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(
            JwtTokenService jwtService,
            EmailSenderService emailSenderService,
            UserRepository userRepository,
            VerificationRepository repository,
            EmailHasher emailHasher,
            RefreshTokenService refreshTokenService) {
        this.jwtService = Objects.requireNonNull(jwtService);
        this.repository = Objects.requireNonNull(repository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.emailHasher = Objects.requireNonNull(emailHasher);
        this.refreshTokenService = Objects.requireNonNull(refreshTokenService);
    }

    @Transactional
    public AuthResponse createUserProfile(CreateUserProfileRequest request) {
        EmailAddress email = new EmailAddress(request.getEmail()); 
        String hashedEmail = emailHasher.hash(email);
        EmailVerification verification = repository.findByEmail(hashedEmail);

        if (verification == null || !verification.isVerified()) {
            throw new IllegalStateException("Email is not verified.");
        }

        User user = new User(UUID.randomUUID(), hashedEmail, Instant.now());
        Profile profile = new Profile(UUID.randomUUID(), user.getId(), request.getUserName(), Instant.now(), Instant.now());

        user.addProfile(profile);

        userRepository.save(user);

        repository.delete(verification);

        String accessToken = jwtService.generateAccessToken(email.getValue(), request.getDeviceId(), profile.getId());
        String refreshToken = refreshTokenService.generateAndStoreRefreshToken(hashedEmail, request.getDeviceId());

        return new AuthResponse(accessToken, refreshToken);
    }

}
