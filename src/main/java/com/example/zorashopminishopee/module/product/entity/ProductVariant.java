package com.example.zorashopminishopee.module.product.entity;

import com.example.zorashopminishopee.module.cart.entity.CartItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "variant", cascade = CascadeType.ALL)
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "variant_name", nullable = false)
    private String variantName;

    @Column(name = "sku", unique = true, nullable = false, length = 100)
    private String sku;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Builder.Default
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "variant")
    private List<CartItem> cartItems;
}
