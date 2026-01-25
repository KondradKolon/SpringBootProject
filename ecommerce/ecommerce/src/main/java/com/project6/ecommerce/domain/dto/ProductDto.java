package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.status;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        Set<Category> categories,
        String image_url,
        status status,
        Double averageRating,
        Integer reviewCount
) {
}
