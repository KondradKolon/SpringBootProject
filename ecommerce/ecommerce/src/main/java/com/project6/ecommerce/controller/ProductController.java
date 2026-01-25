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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(path ="/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Zarządzanie produktami (CRUD)")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    // 1. GET ALL (Lista z paginacją + opcjonalny parametr) - Kod 200
    @Operation(summary = "Pobierz listę produktów", description = "Zwraca paginowaną listę produktów.")
    @io.swagger.v3.oas.annotations.Parameters({
            @Parameter(name = "page", description = "Numer strony (0..N)", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Wielkość strony", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", description = "Sortowanie (np. price,asc)", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "id,asc")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(required = false) String search,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ProductDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // 2. GET BY ID- Kod 200 / 404
    @Operation(summary = "Pobierz szczegóły produktu", description = "Zwraca dane pojedynczego produktu na podstawie ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produkt znaleziony"),
            @ApiResponse(responseCode = "404", description = "Produkt nie istnieje")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        try {
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Kod 404
        }
    }

    // 3. POST - Kod 201
    @Operation(
        summary = "Dodaj nowy produkt", 
        description = "Wymaga multipart/form-data.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                encoding = @io.swagger.v3.oas.annotations.media.Encoding(name = "product", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
        )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProduct (
            @Parameter(
                description = "Dane produktu jako JSON", 
                example = "{\"name\": \"Super Produkt\", \"description\": \"Testowy opis\", \"price\": 199.99, \"quantity\": 10, \"categoryIds\": [1], \"status\": \"AVAILABLE\"}"
            )
            @RequestPart("product") @Valid CreateProductRequestDto createProductRequestDto,
            @Parameter(description = "Opcjonalne zdjęcie produktu")
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        CreateProductRequest createProductRequest = productMapper.fromDto(createProductRequestDto);
        Product product = productService.createProduct(createProductRequest, imageFile);
        ProductDto createdProductDto = productMapper.toDto(product);
        return new ResponseEntity<>(createdProductDto, HttpStatus.CREATED); // Kod 201
    }

    // 4. PUT  - Kod 200 / 404
    @Operation(summary = "Zaktualizuj produkt", description = "Aktualizuje dane produktu (bez zdjęcia).")
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

    // 5. DELETE - Kod 204 / 404
    @Operation(summary = "Usuń produkt", description = "Usuwa produkt z bazy danych.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // Kod 204 (Sukces, brak treści)
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Kod 404
        }
    }
    
    @Operation(summary = "Wyszukaj produkt", description = "Zaawansowane filtrowanie po cenie, kategorii i nazwie.")
    @io.swagger.v3.oas.annotations.Parameters({
            @Parameter(name = "page", description = "Numer strony (0..N)", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Wielkość strony", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "10")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(required = false) String query, // szukana fraza
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Sortowanie (np. price_asc, date_desc)", example = "date_desc") @RequestParam(required = false, defaultValue = "date_desc") String sort,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<ProductDto> result = productService.getFilteredProducts(query, minPrice, maxPrice, categoryId, sort, pageable);
        return ResponseEntity.ok(result);
    }
}