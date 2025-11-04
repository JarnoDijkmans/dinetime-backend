package com.dinetime.identity_service.domain.exceptions;

public class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }
}
