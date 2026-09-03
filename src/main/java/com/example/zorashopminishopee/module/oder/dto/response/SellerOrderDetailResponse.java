package com.example.zorashopminishopee.module.oder.dto.response;

import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerOrderDetailResponse(
        Long orderId,
        String orderNumber,
        StatusType status,
        String note,
        // Thông tin người nhận
        String receiverName,
        String receiverPhone,
        String shippingAddress,
        // Tiền nong
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        // Thanh toán
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,
        LocalDateTime paidAt,
        // Danh sách chi tiết từng món đồ để shop đóng gói
        List<OrderItemResponse> items,
        LocalDateTime createdDate
) {}

