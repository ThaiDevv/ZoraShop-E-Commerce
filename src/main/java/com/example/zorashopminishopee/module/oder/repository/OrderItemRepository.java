package com.example.zorashopminishopee.module.oder.repository;

import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
