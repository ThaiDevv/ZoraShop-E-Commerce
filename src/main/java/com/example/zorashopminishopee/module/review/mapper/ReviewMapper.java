package com.example.zorashopminishopee.module.review.mapper;

import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;
import com.example.zorashopminishopee.module.review.dto.response.ReviewMediaResponse;
import com.example.zorashopminishopee.module.review.dto.response.ReviewUserResponse;
import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import com.example.zorashopminishopee.module.review.entity.ReviewsMedia;
import com.example.zorashopminishopee.module.users.entity.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {
    String maskName(String name){
        int len = name.length();
        if(len == 0){
            return name;
        }
        StringBuilder sb = new StringBuilder(name.length());
        sb.append(name.charAt(0));
        sb.append("*".repeat(6));
        sb.append(name.charAt(len-1));
        return sb.toString();
    }
    List<ReviewMediaResponse> toReviewMediaResponse(ReviewItem reviewItem) {
        List<ReviewsMedia> reviewsMedia = reviewItem.getMedias();
        if (reviewsMedia == null || reviewsMedia.isEmpty()) {
            return null;
        }
        return reviewsMedia.stream().map(
                ReviewsMedia -> new  ReviewMediaResponse(ReviewsMedia.getMediaUrl(), ReviewsMedia.getMediaType())
        ).toList();
    }
    ReviewUserResponse toReviewUserResponse(ReviewItem reviewItem){
        Users user = reviewItem.getUser();
        if(Boolean.TRUE.equals(reviewItem.getIsAnonymous())){
            return new ReviewUserResponse(null, maskName(user.getFullName()), null);
        }
        return new ReviewUserResponse(user.getId(), user.getFullName(), null);
    }
    public ReviewItemResponse toReviewItemResponse(ReviewItem reviewItem) {
        if(reviewItem == null) {
            return null;
        }
        ReviewUserResponse reviewUserResponse = toReviewUserResponse(reviewItem);
        List<ReviewMediaResponse> reviewMediaResponseList = toReviewMediaResponse(reviewItem);
        return new ReviewItemResponse(
                reviewItem.getId(),
                reviewItem.getOrderItem().getVariantName(),
                reviewItem.getStar(),
                reviewItem.getComment(),
                reviewItem.getIsAnonymous(),
                reviewItem.getCountLike(),
                reviewItem.getReplyComment(),
                reviewUserResponse,
                false,
                reviewMediaResponseList,
                reviewItem.getReplyDate(),
                reviewItem.getCreateDate()
        );
    }
}
