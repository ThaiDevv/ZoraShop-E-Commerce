package com.example.zorashopminishopee.module.review.entity;

import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "review")
    private List<ReviewItem> reviewItems =  new ArrayList<>();

    @Builder.Default
    @Column(name = "average_star", nullable = false, precision = 2, scale = 1)
    private BigDecimal averageStar =  BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "count_review", nullable = false)
    private Long countReview = 0L;

    @Builder.Default
    @Column(name = "star_5", nullable = false)
    private Long star5 = 0L;

    @Builder.Default
    @Column(name = "star_4", nullable = false)
    private Long star4 = 0L;

    @Builder.Default
    @Column(name = "star_3", nullable = false)
    private Long star3 = 0L;

    @Builder.Default
    @Column(name = "star_2", nullable = false)
    private Long star2 = 0L;

    @Builder.Default
    @Column(name = "star_1", nullable = false)
    private Long star1 = 0L;

    @Builder.Default
    @Column(name = "review_have_comment", nullable = false)
    private Long reviewHaveComment = 0L;

    @Builder.Default
    @Column(name = "review_have_picture", nullable = false)
    private Long reviewHavePicture = 0L;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
