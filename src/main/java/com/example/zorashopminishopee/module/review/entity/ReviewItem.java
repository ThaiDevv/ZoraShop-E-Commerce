package com.example.zorashopminishopee.module.review.entity;

import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "reviews_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviews_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", unique = true)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    @Column(name = "star", nullable = false)
    private Integer star;

    @Column(name = "comment")
    private String comment;

    @Builder.Default
    @Column(name = "count_like", nullable = false)
    private Long countLike = 0L;

    @Builder.Default
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "reply_comment")
    private String replyComment;

    @Column(name = "reply_date")
    private LocalDateTime replyDate;

    @Builder.Default
    @Column(name = "create_date")
    private LocalDateTime createDate =  LocalDateTime.now();

    @OneToMany(mappedBy = "reviewItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewsMedia> medias = new ArrayList<>();

}
