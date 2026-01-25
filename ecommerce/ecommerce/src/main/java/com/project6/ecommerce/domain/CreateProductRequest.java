package com.project6.ecommerce.domain;

import com.project6.ecommerce.domain.entity.Product.status;

import java.math.BigDecimal;
import java.util.Set;

public record CreateProductRequest (
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        Set<Long> categoryIds,
        String image_url,
        status status
) {}
