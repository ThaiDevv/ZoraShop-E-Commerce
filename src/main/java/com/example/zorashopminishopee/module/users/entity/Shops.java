package com.example.zorashopminishopee.module.users.entity;

import com.example.zorashopminishopee.common.base.BaseEntity;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Shops")
public class Shops extends BaseEntity {

    @OneToOne(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinColumn(name = "seller_id")
    private Users user;

    @OneToMany(
            mappedBy = "shop",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Set<Product> products;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_products")
    private Integer totalProducts;

    @Column(name = "total_followers")
    private Integer totalFollowers;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<Order> orders;
}
