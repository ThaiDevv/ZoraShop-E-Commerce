package com.example.zorashopminishopee.module.oder.repository;

import com.example.zorashopminishopee.module.oder.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH o.shop s
            LEFT JOIN FETCH o.address a
            LEFT JOIN FETCH o.payment p
            WHERE o.id = :orderId AND o.user.email = :email
        """)
    Optional<Order> findDetailByIdAndUserEmail(@Param("orderId") Long orderId, @Param("email") String email);

    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.orderItems oi
            JOIN FETCH oi.variant var
            JOIN FETCH var.inventory i
            WHERE o.user.email = :email AND o.id = :id
        """)
    Optional<Order> findForCancelByUserEmailAndId(@Param("email") String email, @Param("id") Long id);

    Optional<Order> findByIdAndUser_Email(Long id, String email);
}
