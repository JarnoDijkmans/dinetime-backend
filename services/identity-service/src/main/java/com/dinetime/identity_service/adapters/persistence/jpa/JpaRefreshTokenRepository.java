package com.dinetime.identity_service.adapters.persistence.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityRefreshToken;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<EntityRefreshToken, Long> {

    EntityRefreshToken findByUserEmailAndDeviceIdAndToken(String userEmail, String deviceId, String token);

}
