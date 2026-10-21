package com.example.zorashopminishopee.module.review.service;

import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.review.dto.request.CreateReviewRequest;
import com.example.zorashopminishopee.module.review.dto.request.FilterReviewRequest;
import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;
import com.example.zorashopminishopee.module.review.entity.Review;
import org.springframework.data.domain.Page;


public interface ReviewService {
    ReviewItemResponse createReviewItem(String email, Long productId, CreateReviewRequest createReviewRequest);
    Review createReview(Product product);
    Page<ReviewItemResponse> getReviewItems(FilterReviewRequest filterReviewRequest, int page, int size);
}
