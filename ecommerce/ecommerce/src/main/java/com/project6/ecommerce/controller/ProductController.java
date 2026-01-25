package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(path ="/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    // 1. GET ALL (Lista z paginacją + opcjonalny parametr) - Kod 200
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // 2. GET SINGLE (Pojedynczy) - Kod 200 / 404
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        try {
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Kod 404
        }
    }

    // 3. POST (Tworzenie z obrazkiem) - Kod 201
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProduct (
            @RequestPart("product") @Valid CreateProductRequestDto createProductRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        CreateProductRequest createProductRequest = productMapper.fromDto(createProductRequestDto);
        Product product = productService.createProduct(createProductRequest, imageFile);
        // używamy toDto bo nowy produkt nie ma opini
        ProductDto createdProductDto = productMapper.toDto(product);
        return new ResponseEntity<>(createdProductDto, HttpStatus.CREATED); // Kod 201
    }

    // 4. PUT (Aktualizacja danych - JSON) - Kod 200 / 404
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid CreateProductRequestDto productDto // Dane jako JSON
    ) {
        try {
            CreateProductRequest request = productMapper.fromDto(productDto);
            ProductDto updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct); // Kod 200
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Kod 404
        }
    }

    // 5. DELETE (Usuwanie) - Kod 204 / 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // Kod 204 (Sukces, brak treści)
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Kod 404
        }
    }
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(required = false) String query, // szukana fraza
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "date_desc") String sort,
            Pageable pageable
    ) {
        Page<ProductDto> result = productService.getFilteredProducts(query, minPrice, maxPrice, categoryId, sort, pageable);
        return ResponseEntity.ok(result);
    }
}