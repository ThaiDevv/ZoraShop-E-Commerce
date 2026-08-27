package com.example.zorashopminishopee.module.cart.dto.response;

public record CartItemResponse(
    Long id,
    String nameProduct,
    String variantName,
    Integer quantity,
    String imageUrl
) {}
