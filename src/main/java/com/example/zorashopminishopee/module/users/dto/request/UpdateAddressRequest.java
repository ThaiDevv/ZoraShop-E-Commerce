package com.example.zorashopminishopee.module.users.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Partial update: null fields are left unchanged. */
public record UpdateAddressRequest(
        @Size(min = 1, max = 255) String fullName,
        @Pattern(regexp = ValidationPatterns.PHONE, message = "Phone number is invalid") String phone,
        @Size(min = 1, max = 255) String street,
        @Size(min = 1, max = 255) String ward,
        @Size(min = 1, max = 255) String district,
        @Size(min = 1, max = 255) String city,
        Boolean isDefault
) {}
