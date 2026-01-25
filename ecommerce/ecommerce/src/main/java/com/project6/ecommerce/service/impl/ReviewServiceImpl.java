package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Review;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.ReviewRepository;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Review addReview(UUID productId, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setContent(request.content()); // Changed from comment() to content()
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(UUID id) { // Changed from Long to UUID
        reviewRepository.deleteById(id);
    }

    @Override
    public java.util.List<Review> getReviews(UUID productId) {
        return reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId);
    }
}