package com.example.zorashopminishopee.module.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** id = null creates a new image (imageUrl is then required); otherwise updates the existing one. */
public record UpdateProductImageRequest(
        Long id,
        @Size(min = 1, max = 500) String imageUrl,
        @Min(0) Integer sortOrder,
        Boolean isPrimary
) {
}
