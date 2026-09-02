package com.example.zorashopminishopee.module.payment.service;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;

public interface PaymentService {
    Payment createPayment(Order order, PaymentMethod paymentMethod, String transactionId);
    PaymentResponse processPayment(String email, Long orderId);
    PaymentResponse getPaymentStatus(String email, Long orderId);
}
