package com.example.zorashopminishopee.module.payment.strategy;

import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractPaymentStrategy implements PaymentStrategy {
    protected final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse processPayment(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thông tin thanh toán cho đơn hàng: " + order.getId()));
        handle(order, payment);
        return toResponse(payment);
    }

    /** Method-specific processing. Default: nothing to do. */
    protected void handle(Order order, Payment payment) {
    }

    protected static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getTransactionId(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getProvider(),
                payment.getStatus(),
                payment.getPaidAt()
        );
    }
}
