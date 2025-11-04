package com.dinetime.identity_service.adapters.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailValidateRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String code;
}
