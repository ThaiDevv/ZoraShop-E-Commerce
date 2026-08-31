package com.example.zorashopminishopee.module.oder.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long variantId,
        String productName,
        String variantName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {
}
