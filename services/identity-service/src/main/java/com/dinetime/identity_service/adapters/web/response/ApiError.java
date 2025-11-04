package com.dinetime.identity_service.adapters.web.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiError {
    private String code;
    private String message;
}
