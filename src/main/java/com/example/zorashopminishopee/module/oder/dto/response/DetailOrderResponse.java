package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DetailOrderResponse(
        Long orderId,
        Long shopId,
        Long paymentId,
        String orderNumber,
        PaymentMethod method,
        String transactionId,
        String shopName,
        String logoUrl,
        String nameReceive,
        String phoneReceive,
        String address,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        StatusType statusType,
        LocalDateTime createdDate,
        List<OrderItemResponse> orderItemResponses
) {
}
