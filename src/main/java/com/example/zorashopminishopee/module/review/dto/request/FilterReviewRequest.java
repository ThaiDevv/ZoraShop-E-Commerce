package com.example.zorashopminishopee.module.review.dto.request;


public record FilterReviewRequest(
        Long productId,
        boolean haveMedia,
        Integer star
) {
}
