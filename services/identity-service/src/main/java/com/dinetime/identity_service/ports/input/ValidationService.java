package com.dinetime.identity_service.ports.input;

import com.dinetime.identity_service.adapters.web.response.AuthResponse;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;

public interface ValidationService {

    EmailVerificationResponse sendEmailVerificationCode(String email);

    AuthResponse validateEmailCode(String email, String code, String deviceId);

}
