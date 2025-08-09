package com.dinetime.identity_service.adapters.persistence.repository;

import org.springframework.stereotype.Repository;

import com.dinetime.identity_service.adapters.persistence.jpa.JpaRefreshTokenRepository;
import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityRefreshToken;
import com.dinetime.identity_service.domain.model.RefreshToken;
import com.dinetime.identity_service.ports.output.RefreshTokenRepository;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository{

    private final JpaRefreshTokenRepository jpaRepository;

    public RefreshTokenRepositoryImpl(JpaRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken findByUserEmailAndDeviceIdAndToken(String userEmail, String deviceId, String token) {
        EntityRefreshToken entity = jpaRepository.findByUserEmailAndDeviceIdAndToken(userEmail, deviceId, token);
        if (entity == null) {
            return null;
        }
        return mapToDomain(entity);
    }

   @Override
    public void save(RefreshToken refreshToken) {
        EntityRefreshToken entity = mapToEntity(refreshToken);
        jpaRepository.save(entity);
    }

    private RefreshToken mapToDomain(EntityRefreshToken entity) {
        return new RefreshToken(
            entity.getUserEmail(),
            entity.getDeviceId(),
            entity.getToken(),
            entity.getExpiresAt(),
            entity.getLastUsedAt(),
            entity.isRevoked()
        );
    }

    private EntityRefreshToken mapToEntity(RefreshToken domain) {
        return new EntityRefreshToken(
            domain.getUserEmail(),
            domain.getDeviceId(),
            domain.getToken(),
            domain.getExpiresAt(),
            domain.getLastUsedAt(),
            domain.isRevoked()
        );
    }
    
}
