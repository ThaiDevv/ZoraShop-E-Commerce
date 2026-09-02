package com.example.zorashopminishopee.module.payment.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.payment.dto.response.PaymentResponse;
import com.example.zorashopminishopee.module.payment.entity.Payment;
import com.example.zorashopminishopee.module.payment.enums.PaymentMethod;
import com.example.zorashopminishopee.module.payment.enums.PaymentStatus;
import com.example.zorashopminishopee.module.payment.repository.PaymentRepository;
import com.example.zorashopminishopee.module.payment.service.PaymentService;
import com.example.zorashopminishopee.module.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Map<PaymentMethod, PaymentStrategy> strategyMap;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              List<PaymentStrategy> strategies) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getSupportedMethod, Function.identity()));
    }

    @Override
    @Transactional
    public Payment createPayment(Order order, PaymentMethod method, String transactionId) {
        PaymentMethod paymentMethod = method != null ? method : PaymentMethod.COD;
        if (PaymentMethod.COD.equals(paymentMethod)) {
            transactionId = paymentMethod.name() + "-" + order.getOrderNumber();
        }
        Payment payment = Payment.builder()
                .order(order)
                .method(paymentMethod)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .provider(paymentMethod.name())
                .transactionId(transactionId)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(String email, Long orderId) {
        Order order = orderRepository.findByIdAndUser_Email(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền sở hữu của bạn!"));

        if (order.getStatus() == StatusType.CANCELLED) {
            throw new BadRequestException("Đơn hàng #" + order.getOrderNumber() + " đã bị hủy, không thể thanh toán!");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán cho đơn hàng #" + orderId));

        PaymentStrategy strategy = strategyMap.get(payment.getMethod());
        if (strategy == null) {
            throw new BadRequestException("Phương thức thanh toán " + payment.getMethod() + " chưa được hỗ trợ!");
        }

        return strategy.processPayment(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(String email, Long orderId) {
        Order order = orderRepository.findByIdAndUser_Email(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng #" + orderId + " hoặc không thuộc quyền sở hữu của bạn!"));

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin thanh toán cho đơn hàng #" + orderId));

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
