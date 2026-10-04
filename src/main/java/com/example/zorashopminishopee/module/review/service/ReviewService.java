package com.example.zorashopminishopee.module.review.service;

import com.example.zorashopminishopee.module.review.dto.request.CreateReviewRequest;
import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;

public interface ReviewService {
    ReviewItemResponse createReviewItem(String email, CreateReviewRequest createReviewRequest);
}
