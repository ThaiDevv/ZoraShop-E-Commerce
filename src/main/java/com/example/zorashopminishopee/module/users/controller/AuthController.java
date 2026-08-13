package com.example.zorashopminishopee.module.users.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.module.users.dto.request.LoginRequest;
import com.example.zorashopminishopee.module.users.dto.request.RefreshTokenRequest;
import com.example.zorashopminishopee.module.users.dto.request.RegisterRequest;
import com.example.zorashopminishopee.module.users.dto.response.LoginResponse;
import com.example.zorashopminishopee.module.users.dto.response.RegisterResponse;
import com.example.zorashopminishopee.module.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = userService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PostMapping("/refresh-token")
    public  ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken) {
        LoginResponse  response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
