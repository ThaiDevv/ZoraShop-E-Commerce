package com.example.zorashopminishopee.module.users.dto.response;

import com.example.zorashopminishopee.module.oder.enums.Sex;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Sex sex;
    private LocalDate dateOfBirth;
    private Boolean isActive;
    private UserRole role;
}
