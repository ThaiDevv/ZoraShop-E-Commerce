package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductImageRequest(
        @NotBlank @Size(max = 500) String imageUrl,
        @Min(0) Integer sortOrder,
        Boolean isPrimary
) {
}
