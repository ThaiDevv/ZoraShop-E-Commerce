package com.example.zorashopminishopee.module.users.dto.request;

import com.example.zorashopminishopee.module.users.enums.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;

    @Pattern(regexp = ValidationPatterns.PHONE, message = "Phone number is invalid")
    private String phone;

    private Sex sex;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}
