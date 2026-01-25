package com.project6.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Product.status;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    private UUID productId;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        productDto = new ProductDto(productId, "Test Product", "Desc", BigDecimal.TEN, 10, null, "img.jpg", null, 0.0, 0);
    }

    @Test
    void getAllProducts_ShouldReturnOk() throws Exception {
        // Arrange
        Page<ProductDto> page = new PageImpl<>(List.of(productDto));
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void getProductById_ShouldReturnOk_WhenExists() throws Exception {
        // Arrange
        when(productService.getProductById(productId)).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenThrows() throws Exception {
        // Arrange
        when(productService.getProductById(productId)).thenThrow(new RuntimeException("Not Found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldReturnCreated() throws Exception {
        // Arrange
        CreateProductRequestDto requestDto = new CreateProductRequestDto("New", "Desc", BigDecimal.TEN, 5, null, null, status.AVAILABLE);
        String jsonDto = objectMapper.writeValueAsString(requestDto);
        MockMultipartFile jsonPart = new MockMultipartFile("product", "", "application/json", jsonDto.getBytes(StandardCharsets.UTF_8));
        
        when(productMapper.fromDto(any())).thenReturn(mock(CreateProductRequest.class));
        when(productService.createProduct(any(), any())).thenReturn(mock(Product.class));
        when(productMapper.toDto(any())).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/products")
                        .file(jsonPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void updateProduct_ShouldReturnOk_WhenUpdated() throws Exception {
        // Arrange
        CreateProductRequestDto requestDto = new CreateProductRequestDto("Upd", "Desc", BigDecimal.TEN, 5, null, null, status.AVAILABLE);
        when(productService.updateProduct(eq(productId), any())).thenReturn(productDto);

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }
    
    @Test
    void searchProducts_ShouldReturnOk() throws Exception {
        when(productService.getFilteredProducts(any(), any(), any(), any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/products/search")
                        .param("query", "test"))
                .andExpect(status().isOk());
    }
}
