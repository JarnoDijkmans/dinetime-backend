package com.dinetime.identity_service.ports.output;

import java.util.Optional;

import com.dinetime.identity_service.domain.model.User;

public interface UserRepository {

    Optional<User> findByEmail(String hashedEmail);

    void save(User user);
    
}
