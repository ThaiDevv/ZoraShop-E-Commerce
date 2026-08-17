package com.example.zorashopminishopee.module.users.service;

import com.example.zorashopminishopee.module.users.dto.request.*;
import com.example.zorashopminishopee.module.users.dto.response.LoginResponse;
import com.example.zorashopminishopee.module.users.dto.response.RegisterResponse;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);
    UserResponse getUserProfile(Long userId);
    UserResponse getUserProfileByEmail(String email);
    Boolean changePassword(String email, ChangePasswordRequest request);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    LoginResponse loginUser(LoginRequest loginRequest);
    LoginResponse refreshToken(RefreshTokenRequest refreshToken);
    String uploadAvatar(String email, String url);
    Page<UserResponse> getAllUsers(int page, int size);
    UserResponse changeActive(String email);
}
