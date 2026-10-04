package com.example.zorashopminishopee.module.oder.repository;

import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.variant v
        JOIN FETCH oi.order or
        JOIN FETCH or.user
        JOIN FETCH v.product
        WHERE oi.id = :id
        AND oi.order.user.email = :email
   """)
    Optional<OrderItem> findByIdAndUser_Email(@Param("id") Long id,@Param("email") String email);
}
