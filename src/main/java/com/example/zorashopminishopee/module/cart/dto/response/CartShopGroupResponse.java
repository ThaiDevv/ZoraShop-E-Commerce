package com.example.zorashopminishopee.module.cart.dto.response;

import java.util.List;

public record CartShopGroupResponse(
    Long shopId,
    String  shopName,
    String shopLogo,
    List<CartItemResponse> cartItems
) {
}
