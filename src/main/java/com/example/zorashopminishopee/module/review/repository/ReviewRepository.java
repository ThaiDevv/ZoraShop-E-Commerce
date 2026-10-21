package com.example.zorashopminishopee.module.review.repository;

import com.example.zorashopminishopee.module.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
    UPDATE Review r
    SET r.countReview = r.countReview + 1,
        r.star1 = r.star1 + CASE WHEN :star = 1 THEN 1 ELSE 0 END,
        r.star2 = r.star2 + CASE WHEN :star = 2 THEN 1 ELSE 0 END,
        r.star3 = r.star3 + CASE WHEN :star = 3 THEN 1 ELSE 0 END,
        r.star4 = r.star4 + CASE WHEN :star = 4 THEN 1 ELSE 0 END,
        r.star5 = r.star5 + CASE WHEN :star = 5 THEN 1 ELSE 0 END,
        r.reviewHaveComment = r.reviewHaveComment + :comment,
        r.reviewHavePicture = r.reviewHavePicture + :picture,
        r.averageStar = (
            1.0BD * (
                r.star1
                + 2 * r.star2
                + 3 * r.star3
                + 4 * r.star4
                + 5 * r.star5
                + :star
            )
        ) / (r.countReview + 1)
    WHERE r.id = :reviewId
    """)
    int incrementReviewStatistics(
            @Param("reviewId") Long reviewId,
            @Param("star") Integer star,
            @Param("comment") Long comment,
            @Param("picture") Long picture
    );

}
