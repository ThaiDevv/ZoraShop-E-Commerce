package com.example.zorashopminishopee.module.users.dto.response;

import com.example.zorashopminishopee.module.users.dto.request.RegisterRequest;
import com.example.zorashopminishopee.module.users.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private String fullName;
    private String email;
    private String phone;
    public static RegisterResponse of(Users user) {
        return RegisterResponse.builder()
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .build();
    }
}
