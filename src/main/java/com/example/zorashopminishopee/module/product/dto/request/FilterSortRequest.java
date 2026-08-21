package com.example.zorashopminishopee.module.product.dto.request;

import com.example.zorashopminishopee.module.product.emun.ProductSortBy;
import com.example.zorashopminishopee.module.product.emun.ProductSortDir;

import java.math.BigDecimal;

public record FilterSortRequest(
        String keyword,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductSortBy sortBy,
        ProductSortDir sortDir
        ) {
}
