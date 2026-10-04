package com.example.zorashopminishopee.module.review.service.impl;

import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.Order;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.oder.repository.OrderItemRepository;
import com.example.zorashopminishopee.module.oder.repository.OrderRepository;
import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.review.dto.request.CreateReviewRequest;
import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;
import com.example.zorashopminishopee.module.review.entity.Review;
import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import com.example.zorashopminishopee.module.review.repository.ReviewRepository;
import com.example.zorashopminishopee.module.review.service.ReviewService;
import com.example.zorashopminishopee.module.users.entity.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private Review createReview(Product product) {
        Review review = Review.builder()
                .product(product)
                .build();
        product.setReview(review);
        productRepository.save(product);
        return   reviewRepository.save(review);
    }
    @Transactional
    @Override
    public ReviewItemResponse createReviewItem(String email, CreateReviewRequest request) {
        OrderItem orderItem = orderItemRepository.findByIdAndUser_Email(request.orderItemId(), email).orElseThrow(
                () -> new ResourceNotFoundException("Order Item with id " + request.orderItemId() + " not found or you can't reviews the order")
        );
        Product product = orderItem.getVariant().getProduct();
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        Users user = orderItem.getOrder().getUser();
        Review review = product.getReview();
        if(review == null || review.getId() == null) {
            log.error("The review hasn't been created yet. The review create now");
            review = createReview(product);
        }
        ProductVariant productVariant = orderItem.getVariant();
        ReviewItem reviewItem = ReviewItem.builder()
                .review(review)
                .user(user)
                .orderItem(orderItem)
                .productVariant(productVariant)
                .star(request.star())
                .comment(request.comment())
                .isAnonymous(request.isAnonymous())
                .replyComment("")
                .replyDate(null)
                .createDate(LocalDateTime.now())
                .build();
        return null;
    }
}
