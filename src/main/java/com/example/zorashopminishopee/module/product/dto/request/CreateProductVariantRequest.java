package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductVariantRequest(
        @NotBlank String variantName,
        @NotBlank String sku,
        @NotNull BigDecimal price,
        @NotNull Integer stock,
        String imageUrl
) {
}
