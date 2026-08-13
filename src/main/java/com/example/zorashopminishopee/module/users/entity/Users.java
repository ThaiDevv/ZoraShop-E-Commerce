package com.example.zorashopminishopee.module.users.entity;

import com.example.zorashopminishopee.common.base.BaseEntity;
import com.example.zorashopminishopee.module.users.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

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
}
