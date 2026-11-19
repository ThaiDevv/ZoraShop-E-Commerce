package com.example.zorashopminishopee.module.payment.strategy.PaymentStrategyImpl;

import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.strategy.AbstractOnlinePaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class VnPayPaymentStrategy extends AbstractOnlinePaymentStrategy {

    public VnPayPaymentStrategy(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        super(paymentRepository, orderRepository);
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.VNPAY;
    }
}
