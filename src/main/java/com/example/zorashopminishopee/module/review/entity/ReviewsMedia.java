package com.example.zorashopminishopee.module.review.entity;

import com.example.zorashopminishopee.module.review.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class                                                                                                                 ReviewsMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_item_id")
    private ReviewItem reviewItem;

    @Column(name = "media_url")
    private String mediaUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType = MediaType.IMAGE;
}
