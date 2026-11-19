package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @Positive BigDecimal originalPrice,
        @NotEmpty List<@Valid @NotNull CreateProductImageRequest> images,
        @NotEmpty List<@Valid @NotNull CreateProductVariantRequest> variants
) {
}
