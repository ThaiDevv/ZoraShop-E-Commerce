package com.example.zorashopminishopee.module.users.dto.response;

import com.example.zorashopminishopee.module.users.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Boolean isActive;
    private UserRole role;
}
