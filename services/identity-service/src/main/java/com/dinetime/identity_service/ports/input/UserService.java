package com.dinetime.identity_service.ports.input;

import com.dinetime.identity_service.adapters.web.request.CreateUserProfileRequest;
import com.dinetime.identity_service.adapters.web.response.AuthResponse;

public interface UserService {
    AuthResponse createUserProfile(CreateUserProfileRequest request);
}
