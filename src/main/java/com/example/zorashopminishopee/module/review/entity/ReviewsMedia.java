package com.example.zorashopminishopee.module.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_item_id")
    private ReviewItem reviewItem;

    @Column(name = "media_url")
    private String mediaUrl;

    @Builder.Default
    @Column(name = "media_type")
    private String mediaType = "IMAGE";
}
