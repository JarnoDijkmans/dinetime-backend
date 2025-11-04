package com.dinetime.identity_service.ports.input;

import com.dinetime.identity_service.adapters.web.request.EmailVerificationRequest;
import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;

public interface ValidationService {

    EmailVerificationResponse sendEmailVerificationCode(EmailVerificationRequest request);

    boolean validateEmailCode(String email, String code);

}
