package com.example.zorashopminishopee.module.payment.strategy;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;

public interface PaymentStrategy {
    PaymentMethod getSupportedMethod();
    PaymentResponse processPayment(Order order);
}
