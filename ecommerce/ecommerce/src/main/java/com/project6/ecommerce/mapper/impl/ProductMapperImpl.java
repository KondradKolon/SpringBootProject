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
                dto.image_url(),
                dto.status()
        );
    }

    @Override
    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImage_url(),
                product.getStatus()
        );
    }

}
