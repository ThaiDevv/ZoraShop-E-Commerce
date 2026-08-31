package com.example.zorashopminishopee.module.users.entity;

import com.example.zorashopminishopee.common.base.BaseEntity;
import com.example.zorashopminishopee.module.cart.entity.Cart;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends BaseEntity {

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Builder.Default
    @Column(name = "avatar_url")
    private String avatarUrl = "";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.BUYER;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Address> addresses;

    @OneToOne(mappedBy = "user")
    private Shops shops;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}
