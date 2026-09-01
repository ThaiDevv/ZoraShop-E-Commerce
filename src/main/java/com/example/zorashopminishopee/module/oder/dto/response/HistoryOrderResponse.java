package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HistoryOrderResponse(
        Long orderId,
        String orderNumber,
        Long shopId,
        String shopAvatarUrl,
        String shopName,
        BigDecimal totalAmount,
        BigDecimal subTotal,
        StatusType status,
        LocalDateTime createDate,
        List<HistoryOrderItemResponse> items
) {
}
