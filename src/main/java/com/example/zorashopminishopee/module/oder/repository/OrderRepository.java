package com.example.zorashopminishopee.module.oder.repository;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long> , JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}
