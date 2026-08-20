package com.example.zorashopminishopee.module.product.dto.response;

import java.math.BigDecimal;

public record ProductVariantResponse (
        Long id,
        String variantName,
        String sku,
        BigDecimal price,
        Integer stock,
        String imageUrl
){
}
