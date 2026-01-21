package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path ="/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }
    @PostMapping
    public ResponseEntity<ProductDto> createProduct (
            @Valid @RequestBody CreateProductRequestDto createProductRequestDto
    ) {
        CreateProductRequest createProductRequest = productMapper.fromDto(createProductRequestDto);
        Product product = productService.createProduct(createProductRequest);
        ProductDto createProductDto = productMapper.toDto(product);
        return new ResponseEntity<>(createProductDto, HttpStatus.CREATED);
    }


}
