package com.example.zorashopminishopee.module.product.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer soldCount,
        Double ratingAvg,
        Integer ratingCount,
        Long viewCount,
        String status,
        LocalDateTime createdDate,
        ShopSummaryResponse shop,
        CategorySummaryResponse category,
        List<ProductImageResponse> images,
        List<ProductVariantResponse> variants
) {}

