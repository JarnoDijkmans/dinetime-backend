package com.dinetime.identity_service.adapters.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinetime.identity_service.adapters.web.request.EmailValidateRequest;
import com.dinetime.identity_service.adapters.web.request.EmailVerificationRequest;
import com.dinetime.identity_service.adapters.web.response.ApiError;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;
import com.dinetime.identity_service.domain.exceptions.VerificationException;
import com.dinetime.identity_service.ports.input.ValidationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/identity")
@Validated
public class ValidationController {

    private static final Logger log = LoggerFactory.getLogger(ValidationController.class);

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/email-validation/request")
    public ResponseEntity<EmailVerificationResponse> sendVerificationEmail(
            @Valid @RequestBody EmailVerificationRequest request) {
        EmailVerificationResponse response = validationService.sendEmailVerificationCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-validation/validate")
    public ResponseEntity<Boolean> validateEmailCode(
            @Valid @RequestBody EmailValidateRequest request) {
        boolean response = validationService.validateEmailCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ApiError> handleVerificationException(VerificationException ex) {
        log.warn("Verification failed: {}", ex.getMessage());
        ApiError error = new ApiError("verification_failed", "Invalid verification attempt");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        ApiError error = new ApiError("bad_request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiError error = new ApiError("internal_error", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}