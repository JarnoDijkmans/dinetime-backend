package com.dinetime.identity_service.adapters.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityUser;

@Repository
public interface JpaUserRepository extends JpaRepository<EntityUser, UUID> {
    @Query("SELECT u FROM EntityUser u LEFT JOIN FETCH u.profiles WHERE u.hashedEmail = :hashedEmail")
    Optional<EntityUser> findByEmailWithProfiles(@Param("hashedEmail") String hashedEmail);
}
