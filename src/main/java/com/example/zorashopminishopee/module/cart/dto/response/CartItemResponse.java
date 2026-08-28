package com.example.zorashopminishopee.module.cart.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long variantId,
        String sku,
        String productName,
        String variantName,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer quantity,
        Integer stock,
        String imageUrl
) {}

