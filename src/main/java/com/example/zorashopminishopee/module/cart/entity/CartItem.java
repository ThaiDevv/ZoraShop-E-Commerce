package com.example.zorashopminishopee.module.cart.entity;

import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "add_at", updatable = false)
    private LocalDateTime addAt;

    @Column(name = "quantity")
    private Integer quantity;
}
