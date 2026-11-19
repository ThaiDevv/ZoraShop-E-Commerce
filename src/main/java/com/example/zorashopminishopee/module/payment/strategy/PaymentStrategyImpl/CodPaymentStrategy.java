package com.example.zorashopminishopee.module.payment.strategy.PaymentStrategyImpl;

import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.strategy.AbstractPaymentStrategy;
import org.springframework.stereotype.Component;

/** COD is paid on delivery (see OrderServiceImpl#deliverOrder); processing only reports the status. */
@Component
public class CodPaymentStrategy extends AbstractPaymentStrategy {

    public CodPaymentStrategy(PaymentRepository paymentRepository) {
        super(paymentRepository);
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.COD;
    }
}
