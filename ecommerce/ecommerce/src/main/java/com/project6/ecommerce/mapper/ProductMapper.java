package com.project6.ecommerce.mapper;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;

public interface ProductMapper {
    CreateProductRequest fromDto(CreateProductRequestDto dto);
    ProductDto toDto(Product product);
}
