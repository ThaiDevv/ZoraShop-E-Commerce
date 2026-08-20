package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProductImageRequest(
        @NotBlank String imageUrl,
        Integer sortOrder,
        Boolean isPrimary
) {
}
