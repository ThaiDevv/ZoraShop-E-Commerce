package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long orderId,
        String orderNumber,
        String buyerName,
        BigDecimal totalAmount,
        StatusType status,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        int totalItems,
        LocalDateTime createdDate
) {
}
