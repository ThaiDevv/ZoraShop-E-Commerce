package com.example.zorashopminishopee.module.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    @NotBlank
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
}
