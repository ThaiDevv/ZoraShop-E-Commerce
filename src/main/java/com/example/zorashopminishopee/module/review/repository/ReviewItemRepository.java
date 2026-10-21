package com.example.zorashopminishopee.module.review.repository;

import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReviewItemRepository extends JpaRepository<ReviewItem, Long>, JpaSpecificationExecutor<ReviewItem> {
    @Override
    @EntityGraph(attributePaths = {"user", "productVariant"})
    Page<ReviewItem> findAll(Specification<ReviewItem> spec, Pageable pageable);
}
