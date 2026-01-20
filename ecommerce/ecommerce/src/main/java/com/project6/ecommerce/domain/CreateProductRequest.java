package com.project6.ecommerce.domain;

import com.project6.ecommerce.domain.entity.Product.status;

public record CreateProductRequest (
        String name,
        String description,
        double price,
        int quantity,
        String image_url,
        status status
) {}
