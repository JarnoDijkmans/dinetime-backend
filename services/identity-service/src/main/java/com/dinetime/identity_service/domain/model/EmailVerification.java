package com.dinetime.identity_service.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {

    private String hashedEmail;
    private String hashedCode;
    private Instant expiresAt;

}
