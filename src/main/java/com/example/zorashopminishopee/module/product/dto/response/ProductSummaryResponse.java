package com.example.zorashopminishopee.module.product.dto.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal originalPrice,
        String primaryImageUrl,
        Double ratingAvg,
        Integer ratingCount,
        Integer soldCount,
        String shopName
){
}
