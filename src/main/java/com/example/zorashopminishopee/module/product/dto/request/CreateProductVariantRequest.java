package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductVariantRequest(
        @NotBlank @Size(max = 255) String variantName,
        @NotBlank @Size(max = 100) String sku,
        @NotNull @Positive BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        @Size(max = 500) String imageUrl
) {
}
