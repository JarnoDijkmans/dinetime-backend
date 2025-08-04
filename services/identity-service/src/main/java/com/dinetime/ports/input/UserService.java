package com.dinetime.ports.input;

import com.dinetime.identity_service.adapters.web.response.EmailVerificationResponse;

public interface UserService {

    EmailVerificationResponse sendEmailVerificationCode(String email);

}
