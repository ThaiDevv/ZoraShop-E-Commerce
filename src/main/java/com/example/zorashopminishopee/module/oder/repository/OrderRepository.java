package com.example.zorashopminishopee.module.oder.repository;

import com.example.zorashopminishopee.module.oder.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long> , JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query("select o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.variant var " +
            "JOIN FETCH var.inventory i " +
            "WHERE o.user.email =:email " +
            "AND o.id =:id")
    Order findByUser_EmailAndId(@Param("email") String userEmail,@Param("id") Long id);
}
