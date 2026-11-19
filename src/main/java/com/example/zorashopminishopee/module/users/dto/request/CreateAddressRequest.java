package com.example.zorashopminishopee.module.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        @NotBlank(message = "Full name is required") @Size(max = 255) String fullName,
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = ValidationPatterns.PHONE, message = "Phone number is invalid")
        String phone,
        @NotBlank(message = "Street is required") @Size(max = 255) String street,
        @NotBlank(message = "Ward is required") @Size(max = 255) String ward,
        @NotBlank(message = "District is required") @Size(max = 255) String district,
        @NotBlank(message = "City is required") @Size(max = 255) String city,
        Boolean isDefault
) {}
