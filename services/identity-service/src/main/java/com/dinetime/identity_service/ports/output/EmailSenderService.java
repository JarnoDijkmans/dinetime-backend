package com.dinetime.identity_service.ports.output;

public interface EmailSenderService {
    void sendVerificationEmail(String value, String code, String token);
}
