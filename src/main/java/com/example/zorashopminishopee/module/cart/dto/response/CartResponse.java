package com.example.zorashopminishopee.module.cart.dto.response;

import java.math.BigDecimal;

public record CartResponse(
        Long cartId,
        BigDecimal totalAmount,
        Integer totalItem
//        List<CartShopGroupResponse> shopGroups
) {
}
