package com.dinetime.identity_service.adapters.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dinetime.identity_service.adapters.persistence.jpa.JpaUserRepository;
import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityProfile;
import com.dinetime.identity_service.adapters.persistence.jpa.entity.EntityUser;
import com.dinetime.identity_service.domain.model.Profile;
import com.dinetime.identity_service.domain.model.User;
import com.dinetime.identity_service.ports.output.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final JpaUserRepository jpaRepository;

    public UserRepositoryImpl(JpaUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String hashedEmail) {
        return jpaRepository.findByEmailWithProfiles(hashedEmail)
                .map(this::toDomain);
    }


    @Override
    public void save(User user) {
        jpaRepository.save(toEntity(user));
    }

    private User toDomain(EntityUser entity) {
        List<Profile> profiles = entity.getProfiles().stream()
                .map(p -> new Profile(
                        p.getId(),
                        entity.getId(),
                        p.getName(),
                        p.getCreatedAt(),
                        p.getUpdatedAt()
                ))
                .toList();

        return new User(entity.getId(), entity.getHashedEmail(), entity.getCreatedAt(), profiles);
    }

    private EntityUser toEntity(User user) {
        EntityUser entityUser = new EntityUser(user.getId(), user.getHashedEmail(), user.getCreatedAt());

        List<EntityProfile> entityProfiles = (user.getProfiles() != null ? user.getProfiles() : List.<Profile>of())
            .stream()
            .map(profile -> new EntityProfile(
                profile.getId(),
                entityUser,
                profile.getDisplayName(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
            ))
            .toList();

        entityUser.setProfiles(entityProfiles);
        return entityUser;
    }
    
}
