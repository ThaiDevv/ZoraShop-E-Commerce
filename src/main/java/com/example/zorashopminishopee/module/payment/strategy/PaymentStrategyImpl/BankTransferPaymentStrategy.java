package com.example.zorashopminishopee.module.payment.strategy.PaymentStrategyImpl;

import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.strategy.AbstractOnlinePaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class BankTransferPaymentStrategy extends AbstractOnlinePaymentStrategy {

    public BankTransferPaymentStrategy(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        super(paymentRepository, orderRepository);
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.BANK_TRANSFER;
    }
}
