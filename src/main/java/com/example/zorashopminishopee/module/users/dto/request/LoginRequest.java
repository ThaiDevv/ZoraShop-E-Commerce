package com.example.zorashopminishopee.module.users.dto.request;

import lombok.Data;

public record LoginRequest(
         String email,
         String password
) {}
