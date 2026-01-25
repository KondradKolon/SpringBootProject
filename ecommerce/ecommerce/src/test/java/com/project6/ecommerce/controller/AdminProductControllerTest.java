package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateProductRequest;
import com.project6.ecommerce.domain.dto.CreateProductRequestDto;
import com.project6.ecommerce.mapper.ProductMapper;
import com.project6.ecommerce.service.CategoryService;
import com.project6.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // UPDATED
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // UPDATED
import org.springframework.test.context.bean.override.mockito.MockitoBean; // UPDATED
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // UPDATED
    private ProductService productService;

    @MockitoBean // UPDATED
    private CategoryService categoryService;

    @MockitoBean // UPDATED
    private ProductMapper productMapper;

    @Test
    void showAddProductForm_ShouldReturnView() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product-form"))
                .andExpect(model().attributeExists("productDto"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void addProduct_ShouldRedirect_WhenValid() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
        CreateProductRequest request = mock(CreateProductRequest.class);
        
        when(productMapper.fromDto(any(CreateProductRequestDto.class))).thenReturn(request);

        // Act & Assert
        mockMvc.perform(multipart("/admin/products/add")
                        .file(image)
                        .param("name", "Test Product") // Valid fields
                        .param("price", "100.00")
                        .param("status", "AVAILABLE") // Enum
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products/add"))
                .andExpect(flash().attribute("message", "Produkt został dodany pomyślnie!"));

        verify(productService).createProduct(request, image);
    }

    @Test
    void addProduct_ShouldReturnView_WhenValidationFails() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());

        // Act & Assert (Empty name triggers validation error)
        mockMvc.perform(multipart("/admin/products/add")
                        .file(image)
                        .param("name", "") // Invalid
                        .param("price", "100.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product-form"))
                .andExpect(model().attributeHasFieldErrors("productDto", "name"));
    }
    
    @Test
    void addProduct_ShouldReturnView_WhenExceptionOccurs() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
        when(productMapper.fromDto(any())).thenReturn(mock(CreateProductRequest.class));
        doThrow(new RuntimeException("Save failed")).when(productService).createProduct(any(), any());

        // Act & Assert
        mockMvc.perform(multipart("/admin/products/add")
                        .file(image)
                        .param("name", "Test")
                        .param("price", "10.0")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product-form"))
                .andExpect(model().attributeExists("error")); // Controller sets "error" attribute
    }
}
