package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.domain.entity.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review addReview(UUID productId, CreateReviewRequest request);
    void deleteReview(UUID id);
    List<Review> getReviews(UUID productId);
}
