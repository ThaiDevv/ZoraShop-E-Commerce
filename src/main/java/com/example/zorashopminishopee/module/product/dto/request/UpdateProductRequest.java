package com.example.zorashopminishopee.module.product.dto.request;

import com.example.zorashopminishopee.module.product.dto.response.CategorySummaryResponse;
import com.example.zorashopminishopee.module.product.dto.response.ProductVariantResponse;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        String status,
        Long categoryId,
        List<UpdateProductImageRequest> images,
        List<UpdateProductVariantRequest> variants
) {
}
