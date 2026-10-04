package com.example.zorashopminishopee.module.review.repository;

import com.example.zorashopminishopee.module.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
