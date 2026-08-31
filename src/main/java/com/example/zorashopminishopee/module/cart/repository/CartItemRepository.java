package com.example.zorashopminishopee.module.cart.repository;

import com.example.zorashopminishopee.module.cart.entity.Cart;
import com.example.zorashopminishopee.module.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByVariant_Sku(String sku);

    boolean existsByVariant_Sku_AndCart(String variantSku, Cart cart);

    CartItem findByVariant_Sku_AndCart(String sku, Cart cart);

    Optional<CartItem> findByCart_IdAndVariant_Id(Long id, Long id1);

    Optional<CartItem> findById_AndCart_Id(Long id, Long cartId);
    @Query("SELECT item FROM CartItem item " +
            "JOIN FETCH item.variant v " +
            "JOIN FETCH v.product pd " +
            "JOIN FETCH pd.shop sp " +
            "WHERE item.cart.id = :cartId " +
            "ORDER BY sp.id, item.addAt DESC")
    List<CartItem> findByCartIdWithProductAndShop(@Param("cartId") Long cartId);

    @Query("SELECT CI FROM CartItem CI " +
            "JOIN FETCH CI.cart C " +
            "JOIN FETCH C.user us " +
            "JOIN fetch CI.variant v " +
            "JOIN fetch v.product p " +
            "JOIN fetch p.shop s " +
            "where CI.id in :ids " +
            "AND us.email =:email " +
            "ORDER BY s.id, CI.addAt DESC")
    List<CartItem> findByIdAndCart_User_Email(@Param("ids") List<Long> cartIds,@Param("email") String email);


}
