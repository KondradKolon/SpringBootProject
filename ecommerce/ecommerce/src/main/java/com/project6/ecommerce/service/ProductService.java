package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    Product createProduct(CreateProductRequest request, MultipartFile imageFile);
    Page<ProductDto> getAllProducts(Pageable pageable);
    ProductDto getProductById(UUID id);
    ProductDto updateProduct(UUID id, CreateProductRequest request);
    void deleteProduct(UUID id);
    Page<ProductDto> getFilteredProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Long categoryId, String sort, Pageable pageable);
}
