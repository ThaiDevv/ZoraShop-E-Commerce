package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;

import java.time.LocalDateTime;

public record CancelOrderResponse(
        Long orderId,
        String orderNumber,
        StatusType status,
        String reason,
        LocalDateTime cancelAt
) {
}
