package com.example.zorashopminishopee.module.product.dto.request;

public record UpdateProductImageRequest(
        Long id,
        String imageUrl,
        Integer sortOrder,
        Boolean isPrimary
) {
}
