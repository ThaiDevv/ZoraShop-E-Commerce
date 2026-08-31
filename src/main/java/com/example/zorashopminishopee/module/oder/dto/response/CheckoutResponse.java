package com.example.zorashopminishopee.module.oder.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        BigDecimal grandTotal,
        Integer totalOrders,
        List<OrderResponse> orders
) {
}

