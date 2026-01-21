package com.project6.ecommerce.domain;

import com.project6.ecommerce.domain.entity.Category;
import com.project6.ecommerce.domain.entity.Product.status;

import java.util.Set;

public record CreateProductRequest (
        String name,
        String description,
        double price,
        int quantity,
        Set<Long> categoryIds,
        String image_url,
        status status
) {}
