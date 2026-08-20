package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotNull Long categoryId,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal price,
        BigDecimal originalPrice,
        @NotEmpty List<CreateProductImageRequest> images,
        @NotEmpty List<CreateProductVariantRequest> variants
) {
}
