package com.example.zorashopminishopee.module.users.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
