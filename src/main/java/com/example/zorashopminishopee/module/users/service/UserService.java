package com.example.zorashopminishopee.module.users.service;

import com.example.zorashopminishopee.module.users.dto.request.ChangePasswordRequest;
import com.example.zorashopminishopee.module.users.dto.request.LoginRequest;
import com.example.zorashopminishopee.module.users.dto.request.RegisterRequest;
import com.example.zorashopminishopee.module.users.dto.request.UpdateProfileRequest;
import com.example.zorashopminishopee.module.users.dto.response.LoginResponse;
import com.example.zorashopminishopee.module.users.dto.response.RegisterResponse;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);
    UserResponse getUserProfile(Long userId);
    void changePassword(Long userId, ChangePasswordRequest request);
    void updateProfile(Long id, UpdateProfileRequest request);
    LoginResponse loginUser(LoginRequest loginRequest);
}
