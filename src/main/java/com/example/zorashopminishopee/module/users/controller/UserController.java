package com.example.zorashopminishopee.module.users.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.users.dto.request.ChangePasswordRequest;
import com.example.zorashopminishopee.module.users.dto.request.UpdateProfileRequest;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;
import com.example.zorashopminishopee.module.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        return ApiResponse.success(userService.getUserProfileByEmail(email));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();
        return ApiResponse.success(userService.updateProfile(email, request));
    }

    @PutMapping("/me/password")
    public ApiResponse<Boolean> updateUserPassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String email = authentication.getName();
        return ApiResponse.success(userService.changePassword(email, request));
    }

    @PostMapping("/me/avatar")
    public ApiResponse<String> updateUserAvatar(
            Authentication authentication,
            @RequestParam("url") String url
    ) {
        String email = authentication.getName();
        return ApiResponse.success(userService.uploadAvatar(email, url));
    }
}
