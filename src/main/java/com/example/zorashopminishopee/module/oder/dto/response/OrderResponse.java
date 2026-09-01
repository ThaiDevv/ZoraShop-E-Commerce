package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse (
        Long orderId,
        String orderNumber,
        Long shopId,
        String shop_name,
        Long addressId,
        String receiveName,
        String shopUrl,
        String receivePhone,
        String receiveAddress,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        StatusType status,
        String note,
        LocalDateTime createdDate,
        List<OrderItemResponse> items
){
}
