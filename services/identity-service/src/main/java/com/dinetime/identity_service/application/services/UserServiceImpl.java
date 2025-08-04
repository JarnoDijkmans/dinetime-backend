package com.dinetime.identity_service.application.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.identity_service.domain.model.EmailAddress;
import com.dinetime.ports.input.JwtTokenService;
import com.dinetime.ports.input.UserService;
import com.dinetime.ports.output.EmailSenderService;

@Service
public class UserServiceImpl implements UserService {
    private final JwtTokenService jwtService;
    private final EmailSenderService emailSenderService;

    public UserServiceImpl(JwtTokenService jwtService, EmailSenderService emailSenderService) {
        this.jwtService = jwtService;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(String requestEmail) {
        EmailAddress email = new EmailAddress(requestEmail);

        String code = generateVerificationCode();

        String token = jwtService.generateEmailVerificationToken(email.getValue());

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        // EmailVerification verification = new EmailVerification(email, code, expiresAt);

        // repository.save(verification);  

        emailSenderService.sendVerificationEmail(email.getValue(), code, token);

        return new EmailVerificationResponse(
            true,
            "Verification email sent successfully",
            expiresAt
        );
        
    }


    private String generateVerificationCode() {
    SecureRandom random = new SecureRandom();
    int code = 100000 + random.nextInt(900000); 
    return String.valueOf(code);
}
    
}
