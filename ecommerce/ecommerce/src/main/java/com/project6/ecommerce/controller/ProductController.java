package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path ="/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProduct (
            @RequestPart("product") @Valid CreateProductRequestDto createProductRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        CreateProductRequest createProductRequest = productMapper.fromDto(createProductRequestDto);
        Product product = productService.createProduct(createProductRequest, imageFile);;
        ProductDto createProductDto = productMapper.toDto(product);
        return new ResponseEntity<>(createProductDto, HttpStatus.CREATED);
    }


}
