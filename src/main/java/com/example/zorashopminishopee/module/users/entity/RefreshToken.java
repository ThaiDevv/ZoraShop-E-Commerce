package com.example.zorashopminishopee.module.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Builder
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jti", unique = true, nullable = false)
    private String jti;

    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private Boolean isRevoked = Boolean.FALSE;

    @Builder.Default
    @Column(name = "created_date")
    private Instant createDate = Instant.now();

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;
}
