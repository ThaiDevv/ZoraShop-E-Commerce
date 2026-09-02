package com.example.zorashopminishopee.module.payment.strategy.PaymentStrategyImpl;

import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodPaymentStrategy implements PaymentStrategy {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.COD;
    }

    @Override
    public PaymentResponse processPayment(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán cho đơn hàng: " + order.getId()));

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
