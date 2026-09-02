package com.example.zorashopminishopee.module.payment.repository;

import com.example.zorashopminishopee.module.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long id);
    Optional<Payment> findByTransactionId(String transactionId);
}
