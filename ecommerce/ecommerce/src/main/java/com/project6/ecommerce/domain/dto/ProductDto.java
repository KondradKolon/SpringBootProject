package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.Product.status;

import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String description,
        double price,
        int quantity,
        String image_url,
        status status
) {
}
