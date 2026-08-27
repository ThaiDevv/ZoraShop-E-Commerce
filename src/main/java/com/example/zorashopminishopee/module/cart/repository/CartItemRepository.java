package com.example.zorashopminishopee.module.cart.repository;

import com.example.zorashopminishopee.module.cart.entity.Cart;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByVariant_Sku(String sku);

    boolean existsByVariant_Sku_AndCart(String variantSku, Cart cart);

    CartItem findByVariant_Sku_AndCart(String sku, Cart cart);

    Optional<CartItem> findByCart_IdAndVariant_Id(Long id, Long id1);
}
