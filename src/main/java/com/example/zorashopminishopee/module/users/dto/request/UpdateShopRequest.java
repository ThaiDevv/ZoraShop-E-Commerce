package com.example.zorashopminishopee.module.users.dto.request;

import jakarta.validation.constraints.Size;

/** Partial update: null or empty fields are left unchanged. */
public record UpdateShopRequest(
        @Size(max = 255) String name,
        @Size(max = 2000) String description,
        @Size(max = 500) String logoUrl,
        @Size(max = 500) String bannerUrl
) { }
