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

import com.dinetime.identity_service.adapters.web.request.CreateUserProfileRequest;
import com.dinetime.identity_service.adapters.web.response.ApiError;
import com.dinetime.identity_service.adapters.web.response.AuthResponse;
import com.dinetime.identity_service.domain.exceptions.UserException;
import com.dinetime.identity_service.ports.input.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/identity")
@Validated
public class UserController {
     private static final Logger log = LoggerFactory.getLogger(ValidationController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-user/request")
    public ResponseEntity<AuthResponse> createUser(@Valid @RequestBody CreateUserProfileRequest request) {
        AuthResponse response = userService.createUserProfile(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiError> handleVerificationException(UserException ex) {
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
