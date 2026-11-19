package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** id = null creates a new variant (variantName, sku, price, stock are then required); otherwise updates it. */
public record UpdateProductVariantRequest (
        Long id,
        @Size(min = 1, max = 255) String variantName,
        @Size(min = 1, max = 100) String sku,
        @Positive BigDecimal price,
        @PositiveOrZero Integer stock,
        @Size(max = 500) String imageUrl
){
}
