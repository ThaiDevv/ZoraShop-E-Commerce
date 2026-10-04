package com.example.zorashopminishopee.module.review.dto.request;

import java.util.List;

public record CreateReviewRequest(
        Long orderItemId,
        Long productId,
        Integer star,
        String comment,
        boolean isAnonymous,
        List<ReviewsMediaRequest> medias
) {
}
