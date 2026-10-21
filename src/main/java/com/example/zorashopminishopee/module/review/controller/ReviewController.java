package com.example.zorashopminishopee.module.review.controller;

import com.example.zorashopminishopee.common.dto.ApiResponse;
import com.example.zorashopminishopee.common.dto.PageResponse;
import com.example.zorashopminishopee.module.review.dto.request.CreateReviewRequest;
import com.example.zorashopminishopee.module.review.dto.request.FilterReviewRequest;
import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;
import com.example.zorashopminishopee.module.review.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewItemResponse>> createReview(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        ReviewItemResponse response = reviewService.createReviewItem(authentication.getName(), productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewItemResponse>>> getReviews(
            @PathVariable @Positive Long productId,
            @RequestParam(required = false) @Min(1) @Max(5) Integer star,
            @RequestParam(defaultValue = "false") boolean haveMedia,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int size
    ) {
        FilterReviewRequest filter = new FilterReviewRequest(productId, haveMedia, star);
        Page<ReviewItemResponse> reviews = reviewService.getReviewItems(filter, page, size);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromPage(reviews)));
    }
}
