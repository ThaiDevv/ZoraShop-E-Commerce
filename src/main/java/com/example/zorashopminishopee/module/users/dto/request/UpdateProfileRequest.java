package com.example.zorashopminishopee.module.users.dto.request;

import com.example.zorashopminishopee.module.oder.enums.Sex;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    @NotBlank
    private String fullName;
    private String phone;
    private Sex sex;
    private LocalDate birthDate;
}
