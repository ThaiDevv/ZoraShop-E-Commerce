package com.example.zorashopminishopee.module.product.dto.request;

import java.math.BigDecimal;

public record UpdateProductVariantRequest (
        Long id,
        String variantName,
        String sku,
        BigDecimal price,
        Integer stock,
        String imageUrl
){
}
