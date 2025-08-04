package com.dinetime.identity_service.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {

    private EmailAddress email;
    private String code;
    private LocalDateTime expiresAt;

}
