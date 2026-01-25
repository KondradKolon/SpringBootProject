package com.project6.ecommerce.mapper.impl;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.mapper.ProductMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public CreateProductRequest fromDto(CreateProductRequestDto dto) {
        return new CreateProductRequest(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.quantity(),
                dto.categoryIds(),
                dto.image_url(),
                dto.status()
        );
    }

    @Override
    public ProductDto toDto(Product product) {
        return toDtoWithRating(product, 0.0, 0);
    }
    public ProductDto toDtoWithRating(Product product, Double averageRating, Integer reviewCount) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategories(),
                product.getImage_url(), 
                product.getStatus(),
                averageRating,
                reviewCount
        );
    }

}
