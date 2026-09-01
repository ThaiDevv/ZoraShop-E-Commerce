package com.example.zorashopminishopee.module.oder.dto.response;

import java.math.BigDecimal;

public record HistoryOrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        String productPictureUrl,
        String variantName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subTotal
) {
}
