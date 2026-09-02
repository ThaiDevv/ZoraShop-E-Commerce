package com.example.zorashopminishopee.module.payment.strategy.PaymentStrategyImpl;

import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MomoPaymentStrategy implements PaymentStrategy {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public PaymentResponse processPayment(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán cho đơn hàng: " + order.getId()));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            if (order.getStatus() == StatusType.PENDING) {
                order.setStatus(StatusType.CONFIRMED);
                orderRepository.save(order);
            }
        }

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
