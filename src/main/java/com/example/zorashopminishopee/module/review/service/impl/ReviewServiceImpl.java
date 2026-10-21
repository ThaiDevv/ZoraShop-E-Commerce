package com.example.zorashopminishopee.module.review.service.impl;

import com.example.zorashopminishopee.common.exception.BadRequestException;
import com.example.zorashopminishopee.common.exception.ConflictRequestException;
import com.example.zorashopminishopee.common.exception.ResourceNotFoundException;
import com.example.zorashopminishopee.module.oder.entity.OrderItem;
import com.example.zorashopminishopee.module.oder.enums.StatusType;
import com.example.zorashopminishopee.module.oder.repository.OrderItemRepository;
import com.example.zorashopminishopee.module.product.entity.Product;
import com.example.zorashopminishopee.module.product.entity.ProductVariant;
import com.example.zorashopminishopee.module.product.repository.ProductRepository;
import com.example.zorashopminishopee.module.review.dto.request.CreateReviewRequest;
import com.example.zorashopminishopee.module.review.dto.request.FilterReviewRequest;
import com.example.zorashopminishopee.module.review.dto.response.ReviewItemResponse;
import com.example.zorashopminishopee.module.review.entity.Review;
import com.example.zorashopminishopee.module.review.entity.ReviewItem;
import com.example.zorashopminishopee.module.review.entity.ReviewsMedia;
import com.example.zorashopminishopee.module.review.mapper.ReviewMapper;
import com.example.zorashopminishopee.module.review.repository.ReviewItemRepository;
import com.example.zorashopminishopee.module.review.repository.ReviewRepository;
import com.example.zorashopminishopee.module.review.service.ReviewService;
import com.example.zorashopminishopee.module.review.specification.ReviewSpecification;
import com.example.zorashopminishopee.module.users.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewItemRepository reviewItemRepository;
    @Transactional
    public Review createReview(Product product) {
        Review review = Review.builder()
                .product(product)
                .build();
        product.setReview(review);
        return   reviewRepository.save(review);
    }
    @Transactional(readOnly = true)
    @Override
    public Page<ReviewItemResponse> getReviewItems(FilterReviewRequest filterReviewRequest, int page, int size) {
        Sort sort = Sort.by(
                Sort.Order.desc("createDate"),
                Sort.Order.desc("star")
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<ReviewItem> specification = Specification
                .where(ReviewSpecification.findStar(filterReviewRequest.star())
                        .and(ReviewSpecification.haveMedia(filterReviewRequest.haveMedia()))
                        .and(((root, query, criteriaBuilder) -> {
                            return criteriaBuilder.equal(root.get("review").get("product").get("id"), filterReviewRequest.productId());
                        }))
                );
        Page<ReviewItem> reviewItemPage= reviewItemRepository.findAll(specification, pageable);
        return reviewItemPage.map(reviewMapper::toReviewItemResponse);
    }
    private void  updateReview(Review review, ReviewItem reviewItem) {
        int star = reviewItem.getStar();
        String comment = reviewItem.getComment();
        boolean haveComment = comment != null && !comment.isBlank();
        boolean haveMedia = reviewItem.getMedias()  != null && !reviewItem.getMedias().isEmpty();
        int count = reviewRepository.incrementReviewStatistics(review.getId(), star, haveComment ? 1L : 0L, haveMedia ? 1L : 0L);
        if(count != 1) {
            throw new IllegalStateException("Review haven't updated");
        }
    }
    @Transactional
    @Override
    public ReviewItemResponse createReviewItem(String email, Long productId, CreateReviewRequest request) {
        OrderItem orderItem = orderItemRepository.findByIdAndUser_Email(request.orderItemId(), email).orElseThrow(
                () -> new ResourceNotFoundException("Order Item with id " + request.orderItemId() + " not found or you can't reviews the order")
        );
        if (!productId.equals(orderItem.getProductId())) {
            throw new BadRequestException("Order item does not belong to product " + productId);
        }
        if(!StatusType.DELIVERED.equals(orderItem.getOrder().getStatus())) {
            throw new BadRequestException("Status isn't DELIVERED");
        }
        if(orderItem.getReviewItem() != null) {
            throw new ConflictRequestException("Order Item have been reviewed");
        }
        Product product = orderItem.getVariant().getProduct();
        Users user = orderItem.getOrder().getUser();
        Review review = product.getReview();
        if (review == null || review.getId() == null) {
            throw new IllegalStateException("Review summary is missing for product " + product.getId());
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
                .createDate(LocalDateTime.now())
                .build();
        if (request.medias() != null) {
            request.medias().forEach(
                    reviewsMediaRequest ->
                            reviewItem.getMedias().add(
                                    ReviewsMedia.builder()
                                            .mediaUrl(reviewsMediaRequest.mediaUrl())
                                            .mediaType(reviewsMediaRequest.type())
                                            .reviewItem(reviewItem)
                                            .build()
                            )
            );
        }

        ReviewItem saved;
        try {
            saved = reviewItemRepository.save(reviewItem);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictRequestException("Order item has already been reviewed", e.getCause());
        }
        updateReview(review, saved);
        return reviewMapper.toReviewItemResponse(reviewItem);
    }
}
