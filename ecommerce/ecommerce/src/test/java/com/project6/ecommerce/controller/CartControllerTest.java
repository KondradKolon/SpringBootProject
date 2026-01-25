package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Cart cart;

    @MockitoBean
    private ProductService productService;

    @Test
    void showCart_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/view"))
                .andExpect(model().attributeExists("cart")); 
    }

    @Test
    void addToCart_ShouldRedirect() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        when(productService.getProductById(productId)).thenReturn(mock(ProductDto.class));

        // Act & Assert
        mockMvc.perform(post("/cart/add")
                        .param("productId", productId.toString())
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cart).addItem(any(ProductDto.class), eq(2));
    }

    @Test
    void updateQuantity_ShouldRedirect() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/cart/update")
                        .param("productId", productId.toString())
                        .param("quantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cart).updateQuantity(productId, 5);
    }

    @Test
    void removeFromCart_ShouldRedirect() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/cart/remove/{id}", productId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cart).removeItem(productId);
    }
}
