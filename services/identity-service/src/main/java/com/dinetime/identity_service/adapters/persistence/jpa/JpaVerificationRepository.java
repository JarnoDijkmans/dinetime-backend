package com.dinetime.identity_service.adapters.persistence.jpa;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityEmailVerification;

import jakarta.transaction.Transactional;

@Repository
public interface JpaVerificationRepository extends JpaRepository<EntityEmailVerification, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE EntityEmailVerification e SET e.code = :code, e.expiresAt = :expiresAt WHERE e.email = :email")
    int updateCodeAndExpiryByEmail(@Param("code") String code, @Param("expiresAt") Instant expiresAt, @Param("email") String email);

    EntityEmailVerification findByEmail(String hashedEmail);    
}
