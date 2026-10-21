package com.example.zorashopminishopee.module.review.specification;

import com.example.zorashopminishopee.module.review.entity.Review;
import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification {
    public static Specification<ReviewItem> findStar(Integer star) {
        return (root, query, cb) -> star == null ? null : cb.equal(root.get("star"), star);
    }
    public static Specification<ReviewItem> haveMedia(boolean haveMedia) {
        return (root, query, cb) -> haveMedia ? cb.isNotEmpty(root.get("medias")) : null;
    }
}
