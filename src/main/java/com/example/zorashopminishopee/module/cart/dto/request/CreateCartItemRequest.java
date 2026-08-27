package com.example.zorashopminishopee.module.cart.dto.request;

public record CreateCartItemRequest(
        String sku,
        Integer quantity
) {
}
