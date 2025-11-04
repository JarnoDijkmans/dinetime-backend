package com.dinetime.identity_service.adapters.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dinetime.identity_service.adapters.persistence.jpa.JpaVerificationRepository;
import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityEmailVerification;
import com.dinetime.identity_service.domain.model.EmailVerification;
import com.dinetime.identity_service.ports.output.VerificationRepository;

@Repository
public class VerificationRepositoryImpl implements VerificationRepository {

    private final JpaVerificationRepository jpaRepository;

    public VerificationRepositoryImpl(JpaVerificationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(EmailVerification verification) {
        int updated = jpaRepository.updateCodeExpiryAndVerifiedByEmail(
                verification.getHashedCode(),
                verification.getExpiresAt(),
                verification.isVerified(),
                verification.getHashedEmail());
        if (updated == 0) {
            jpaRepository.save(new EntityEmailVerification(
                    verification.getHashedEmail(),
                    verification.getHashedCode(),
                    verification.getExpiresAt(),
                    verification.isVerified()));
        }
    }

    @Override
    public EmailVerification findByEmail(String hashedEmail) {
        EntityEmailVerification entity = jpaRepository.findByEmail(hashedEmail);
        if (entity == null) {
            return null;
        }
        return mapToDomain(entity);
    }

    private EmailVerification mapToDomain(EntityEmailVerification entity) {
        return new EmailVerification(
                entity.getId(),
                entity.getEmail(),
                entity.getCode(),
                entity.getExpiresAt(),
                entity.isVerified());
    }

    private EntityEmailVerification mapToEntity(EmailVerification domain) {
        return new EntityEmailVerification(
                domain.getId(),
                domain.getHashedEmail(),
                domain.getHashedCode(),
                domain.getExpiresAt(),
                domain.isVerified());
    }

    @Override
    public void delete(EmailVerification verification) {
        Optional<EntityEmailVerification> entityOpt = jpaRepository.findById(verification.getId());
        entityOpt.ifPresent(jpaRepository::delete);
    }

}
