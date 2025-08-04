package com.dinetime.ports.output;

public interface EmailSenderService {
    void sendVerificationEmail(String value, String code, String token);
}
