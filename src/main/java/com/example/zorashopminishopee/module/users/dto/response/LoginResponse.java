package com.example.zorashopminishopee.module.users.dto.response;



public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username
) {}
