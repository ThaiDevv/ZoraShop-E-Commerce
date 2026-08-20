package com.example.zorashopminishopee.module.product.dto.response;

public record ProductImageResponse(
        Long id,
        String imageUrl,
        Integer sortOrder,
        Boolean isPrimary
) {
}
