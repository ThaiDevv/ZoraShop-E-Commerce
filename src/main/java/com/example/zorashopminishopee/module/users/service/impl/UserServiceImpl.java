package com.example.zorashopminishopee.module.users.service.impl;

import com.example.zorashopminishopee.common.exception.DuplicateResourceException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.common.exception.UnauthorizedException;
import com.example.zorashopminishopee.module.users.dto.request.*;
import com.example.zorashopminishopee.module.users.dto.response.LoginResponse;
import com.example.zorashopminishopee.module.users.dto.response.RefreshTokenResponse;
import com.example.zorashopminishopee.module.users.dto.response.RegisterResponse;
import com.example.zorashopminishopee.module.users.dto.response.UserResponse;
import com.example.zorashopminishopee.module.users.entity.RefreshToken;
import com.example.zorashopminishopee.module.users.entity.Users;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import com.example.zorashopminishopee.module.users.repository.RefreshTokenRepository;
import com.example.zorashopminishopee.module.users.repository.UserRepository;
import com.example.zorashopminishopee.module.users.service.UserService;
import com.example.zorashopminishopee.security.CustomUserDetails;
import com.example.zorashopminishopee.security.CustomUserDetailsService;
import com.example.zorashopminishopee.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;
    private final RefreshTokenRepository refreshTokenRepository;
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
                .dateOfBirth(registerRequest.getBirthDay())
                .sex(registerRequest.getSex())
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
                .dateOfBirth(user.getDateOfBirth())
                .sex(user.getSex())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfileByEmail(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return UserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .sex(user.getSex())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

    @Override
    @Transactional
    public Boolean changePassword(String email, ChangePasswordRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UnauthorizedException("Old password doesn't match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateResourceException("Phone already exists");
            }
            user.setPhone(request.getPhone());
        }

        user.setFullName(request.getFullName());
        user.setSex(request.getSex());
        user.setDateOfBirth(request.getBirthDate());
        userRepository.save(user);

        return UserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .dateOfBirth(user.getDateOfBirth())
                .sex(user.getSex())
                .build();
    }

    @Transactional
    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
            CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
            String refreshToken = issueRefreshToken(details.getUser(), details);
            String accessToken = issueAccessToken(details);
            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    authentication.getName()
            );

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Wrong email or password");
        }
    }
    private String issueAccessToken(UserDetails userDetails){
        Instant now = Instant.now();
        Instant expirationAccess = now.plusMillis(accessExpiration);
        String jtiAccess = UUID.randomUUID().toString();
        return jwtTokenProvider.generateAccessToken(userDetails, jtiAccess, Date.from(expirationAccess));
    }
    private String issueRefreshToken ( Users user, UserDetails userDetails){
        Instant now = Instant.now();
        Instant expirationRefresh = now.plusMillis(refreshExpiration);
        String jtiRefresh = UUID.randomUUID().toString();
        RefreshToken reToken = RefreshToken.builder()
                .jti(jtiRefresh)
                .expirationDate(expirationRefresh)
                .user(user)
                .build();
        refreshTokenRepository.save(reToken);
        return jwtTokenProvider.generateRefreshToken(userDetails, jtiRefresh, Date.from(expirationRefresh));
    }
    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parseRefreshToken(refreshToken.getRefreshToken());
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(claims.getSubject());
            Users users = userDetails.getUser();
            String refreshToken1 = issueRefreshToken(users, userDetails);
            String accessToken = issueAccessToken(userDetails);
            return new RefreshTokenResponse(accessToken, refreshToken1);
        } catch (JwtException e) {
            throw new UnauthorizedException("Token is invalid");
        }
    }
    @Override
    @Transactional
    public String uploadAvatar(String email, String url) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        user.setAvatarUrl(url);
        userRepository.save(user);
        return url;
    }

    @Override
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = userRepository.findAll(pageable);
        Page<UserResponse> userResponseList = users
                .map(
                        user ->  UserResponse.builder()
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .avatarUrl(user.getAvatarUrl())
                                .isActive(user.getIsActive())
                                .role(user.getRole())
                                .sex(user.getSex())
                                .dateOfBirth(user.getDateOfBirth())
                                .build()
                );
        return userResponseList;
    }

    @Override
    @Transactional
    public UserResponse changeActive(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        user.setIsActive(!user.getIsActive());
        return UserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .build();
    }
}
