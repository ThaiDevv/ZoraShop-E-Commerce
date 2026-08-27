package com.example.zorashopminishopee.module.cart.repository;

import com.example.zorashopminishopee.module.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser_Email(String userEmail);
}
