package com.example.zorashopminishopee.module.payment.strategy;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;

import java.time.LocalDateTime;

/**
 * Mock online payment: marks the payment COMPLETED and auto-confirms a PENDING order.
 * Replace {@link #handle} per provider when a real gateway is integrated.
 */
public abstract class AbstractOnlinePaymentStrategy extends AbstractPaymentStrategy {
    protected final OrderRepository orderRepository;

    protected AbstractOnlinePaymentStrategy(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        super(paymentRepository);
        this.orderRepository = orderRepository;
    }

    @Override
    protected void handle(Order order, Payment payment) {
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return;
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
        if (order.getStatus() == StatusType.PENDING) {
            order.setStatus(StatusType.CONFIRMED);
            orderRepository.save(order);
        }
    }
}
