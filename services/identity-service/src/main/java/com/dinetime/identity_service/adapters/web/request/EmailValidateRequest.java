package com.dinetime.identity_service.adapters.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailValidateRequest {
    private String email;
    private String code;
    private String deviceId;
}
