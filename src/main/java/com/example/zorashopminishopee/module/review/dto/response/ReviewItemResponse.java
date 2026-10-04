package com.example.zorashopminishopee.module.review.dto.response;

import com.example.zorashopminishopee.module.review.enums.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ReviewItemResponse(
        Long id,
        String variantName,
        Integer star,
        String comment,
        boolean isAnonymous,
        Long countLike,
        String replyComment,
        ReviewUserResponse user,
        Boolean isLiked,
        List<ReviewMediaResponse> mediaUrl,
        LocalDateTime replyDate,
        LocalDateTime createDate
) {
}
