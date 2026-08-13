package com.example.zorashopminishopee.module.users.service.impl;

import com.example.zorashopminishopee.common.exception.DuplicateResourceException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.common.exception.UnauthorizedException;
import com.example.zorashopminishopee.module.users.dto.request.ChangePasswordRequest;
import com.example.zorashopminishopee.module.users.dto.request.LoginRequest;
import com.example.zorashopminishopee.module.users.dto.request.RegisterRequest;
import com.example.zorashopminishopee.module.users.dto.request.UpdateProfileRequest;
import com.example.zorashopminishopee.module.users.dto.response.LoginResponse;
import com.example.zorashopminishopee.module.users.dto.response.RegisterResponse;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import com.example.zorashopminishopee.module.users.service.UserService;
import com.example.zorashopminishopee.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new DuplicateResourceException("Phone already exists");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        Users newUser = Users.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullname())
                .password(encodedPassword)
                .phone(registerRequest.getPhone())
                .role(UserRole.BUYER)
                .isActive(true)
                .build();

        userRepository.save(newUser);
        return RegisterResponse.of(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return UserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Old password doesn't match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Phone already exists");
            }
            user.setPhone(request.getPhone());
        }

        user.setFullName(request.getFullName());
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
    }
    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.email(),
                                    loginRequest.password()
                            )
                    );

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            String token = jwtTokenProvider.generateToken(userDetails);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
            return new LoginResponse(
                    token,
                    refreshToken,
                    userDetails.getUsername()
            );

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Wrong email or password");
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try{
            String email = jwtTokenProvider.getEmailFromRefreshToken(refreshToken);
            UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(email);
            String  token = jwtTokenProvider.generateToken(userDetails);
            LoginResponse response = new LoginResponse(token, refreshToken, userDetails.getUsername());
            return response;
        }catch (Exception e){
            throw new UnauthorizedException("Refresh token is invalid");
        }
    }

}
