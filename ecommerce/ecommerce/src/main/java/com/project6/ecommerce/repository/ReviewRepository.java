package com.project6.ecommerce.repository;

import com.project6.ecommerce.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByProductIdOrderByCreatedAtDesc(UUID productId);
}