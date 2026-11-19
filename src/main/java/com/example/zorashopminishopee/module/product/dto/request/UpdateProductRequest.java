package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** Partial update: null fields are left unchanged. */
public record UpdateProductRequest(
        @Size(min = 1, max = 255) String name,
        String description,
        @Positive BigDecimal price,
        @Positive BigDecimal originalPrice,
        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Seller can only set status to ACTIVE or INACTIVE")
        String status,
        Long categoryId,
        List<@Valid @NotNull UpdateProductImageRequest> images,
        List<@Valid @NotNull UpdateProductVariantRequest> variants
) {
}
