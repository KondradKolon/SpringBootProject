package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Product.status;
import com.project6.ecommerce.mapper.impl.ProductMapperImpl;
import com.project6.ecommerce.repository.CategoryRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.ReviewRepository;
import com.project6.ecommerce.service.impl.ProductServiceImpl;
import com.project6.ecommerce.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private ProductMapperImpl productMapper;
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        product.setQuantity(10);
        product.setStatus(status.AVAILABLE);
        
        productDto = new ProductDto(productId, "Test Product", "Desc", BigDecimal.TEN, 10, null, null, status.AVAILABLE, 0.0, 0);
    }



    @Test
    void getFilteredProducts_ShouldCallRepositoryWithSpec() {
        // Arrange
        String search = "test";
        BigDecimal minPrice = BigDecimal.ONE;
        BigDecimal maxPrice = BigDecimal.TEN;
        Long categoryId = 1L;
        String sort = "price_asc";
        Pageable pageable = PageRequest.of(0, 10);
        
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toDto(any())).thenReturn(productDto);

        // Act
        Page<ProductDto> result = productService.getFilteredProducts(search, minPrice, maxPrice, categoryId, sort, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getFilteredProducts_ShouldHandleSortingOptions() {
        // Arrange
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act & Assert (Verification of no exceptions for different sort keys)
        productService.getFilteredProducts(null, null, null, null, "price_desc", PageRequest.of(0, 10));
        productService.getFilteredProducts(null, null, null, null, "date_desc", PageRequest.of(0, 10));
        
        verify(productRepository, times(2)).findAll(any(Specification.class), any(Pageable.class));
    }
    @Test
    void createProduct_ShouldSaveProduct_WhenValid() {
        // Arrange

        CreateProductRequest request = new CreateProductRequest("New", "Desc", BigDecimal.TEN, 5, Set.of(), null, status.AVAILABLE);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.createProduct(request, null);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoriesMissing() {
        // Arrange
        Set<Long> catIds = Set.of(99L);
        CreateProductRequest request = new CreateProductRequest("New", "Desc", BigDecimal.TEN, 5, catIds, null, status.AVAILABLE);
        when(categoryRepository.findAllById(catIds)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.createProduct(request, null));
    }

    @Test
    void updateProduct_ShouldUpdateFields_WhenProductExists() {
        
        CreateProductRequest request = new CreateProductRequest("Updated", "Desc", BigDecimal.ONE, 1, null, null, status.NOT_AVAILABLE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0)); 
        

        when(productMapper.toDtoWithRating(any(), anyDouble(), anyInt())).thenReturn(new ProductDto(productId, "Updated", "Desc", BigDecimal.ONE, 1, null, null, status.NOT_AVAILABLE, 0.0, 0));


        ProductDto result = productService.updateProduct(productId, request);


        assertEquals("Updated", result.name());
        assertEquals(status.NOT_AVAILABLE, result.status());
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        CreateProductRequest request = new CreateProductRequest("U", "D", BigDecimal.TEN, 1, null, null, status.AVAILABLE);
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(UUID.randomUUID(), request));
    }

    @Test
    void deleteProduct_ShouldDelete_WhenExists() {
        when(productRepository.existsById(productId)).thenReturn(true);
        
        productService.deleteProduct(productId);
        
        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenNotFound() {
        when(productRepository.existsById(productId)).thenReturn(false);
        
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(productId));
    }
}
