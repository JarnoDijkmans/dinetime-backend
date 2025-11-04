package com.dinetime.identity_service.ports.output;

import com.dinetime.identity_service.domain.model.EmailVerification;

public interface VerificationRepository {

    void save(EmailVerification verification);

    EmailVerification findByEmail(String hashedEmail);

    void delete(EmailVerification verification);

}
