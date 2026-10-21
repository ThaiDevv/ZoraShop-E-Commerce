package com.example.zorashopminishopee.module.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewItemResponse(
        Long id,
        String variantName,
        Integer star,
        String comment,
        boolean isAnonymous,
        Long countLike,
        String replyComment,
        ReviewUserResponse user,
        boolean isLiked,
        List<ReviewMediaResponse> medias,
        LocalDateTime replyDate,
        LocalDateTime createDate
) {
}
