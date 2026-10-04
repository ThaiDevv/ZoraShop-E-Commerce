package com.example.zorashopminishopee.module.review.repository;

import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewItemRepository extends JpaRepository<ReviewItem, Long> {
}
