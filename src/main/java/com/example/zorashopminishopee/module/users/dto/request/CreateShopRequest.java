package com.example.zorashopminishopee.module.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShopRequest(
        @NotBlank(message = "Shop name is required")
        @Size(max = 255)
        String name,
        @Size(max = 2000)
        String description
) {
}
