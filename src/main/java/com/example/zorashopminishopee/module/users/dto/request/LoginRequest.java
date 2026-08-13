package com.example.zorashopminishopee.module.users.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
