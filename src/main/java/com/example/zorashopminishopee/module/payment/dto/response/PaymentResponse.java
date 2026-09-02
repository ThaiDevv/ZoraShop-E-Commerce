package com.example.zorashopminishopee.module.payment.dto.response;

import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        String transactionId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String provider,
        PaymentStatus status,
        LocalDateTime paidAt
) {
}
