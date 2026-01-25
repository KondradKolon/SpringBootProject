package com.project6.ecommerce.mapper;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Product.status;
import com.project6.ecommerce.mapper.impl.ProductMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapperImpl productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapperImpl();
    }

    @Test
    void fromDto_ShouldReturnRequest() {
        // Arrange
        CreateProductRequestDto dto = new CreateProductRequestDto(
                "Test Product",
                "Description",
                BigDecimal.valueOf(100.0),
                10,
                Set.of(1L, 2L),
                "image.jpg",
                status.AVAILABLE
        );

        // Act
        CreateProductRequest request = productMapper.fromDto(dto);

        // Assert
        assertNotNull(request);
        assertEquals(dto.name(), request.name());
        assertEquals(dto.description(), request.description());
        assertEquals(dto.price(), request.price());
        assertEquals(dto.quantity(), request.quantity());
        assertEquals(dto.categoryIds(), request.categoryIds());
        assertEquals(dto.image_url(), request.image_url());
        assertEquals(dto.status(), request.status());
    }

    @Test
    void toDto_ShouldReturnDto_WithDefaultValues() {
        // Arrange
        UUID id = UUID.randomUUID();
        Product product = new Product(
                id,
                "Test Product",
                "Description",
                BigDecimal.valueOf(100.0),
                10,
                new HashSet<>(),
                "image.jpg",
                status.AVAILABLE
        );

        // Act
        ProductDto dto = productMapper.toDto(product);

        // Assert
        assertNotNull(dto);
        assertEquals(product.getId(), dto.id());
        assertEquals(product.getName(), dto.name());
        assertEquals(product.getDescription(), dto.description());
        assertEquals(product.getPrice(), dto.price());
        assertEquals(product.getQuantity(), dto.quantity());
        assertEquals(product.getCategories(), dto.categories());
        assertEquals(product.getImage_url(), dto.image_url());
        assertEquals(product.getStatus(), dto.status());
        assertEquals(0.0, dto.averageRating());
        assertEquals(0, dto.reviewCount());
    }

    @Test
    void toDtoWithRating_ShouldReturnDto_WithProvidedRating() {
        // Arrange
        UUID id = UUID.randomUUID();
        Product product = new Product(
                id,
                "Test Product",
                "Description",
                BigDecimal.valueOf(100.0),
                10,
                new HashSet<>(),
                "image.jpg",
                status.AVAILABLE
        );
        Double averageRating = 4.5;
        Integer reviewCount = 10;

        // Act
        ProductDto dto = productMapper.toDtoWithRating(product, averageRating, reviewCount);

        // Assert
        assertNotNull(dto);
        assertEquals(averageRating, dto.averageRating());
        assertEquals(reviewCount, dto.reviewCount());
    }
}
