package com.example.zorashopminishopee.module.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        BigDecimal totalAmount,
        Integer totalItem,
        List<CartShopGroupResponse>shopGroups
) {
}
