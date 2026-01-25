package com.project6.ecommerce.domain;

public record CreateReviewRequest(
        Integer rating, // 1-5
        String content
) {}