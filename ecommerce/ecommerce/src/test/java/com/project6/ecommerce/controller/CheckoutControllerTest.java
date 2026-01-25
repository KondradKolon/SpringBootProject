package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import com.project6.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
 // Corrected Import via boot.test...
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Cart cart;

    @MockitoBean // UPDATED
    private OrderService orderService;

    @Test
    void showCheckout_ShouldRedirect_WhenCartEmpty() throws Exception {
        when(cart.getItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void showCheckout_ShouldReturnView_WhenCartNotEmpty() throws Exception {
        // Prepare CartItem with Product data for Thymeleaf rendering
        ProductDto productDto = new ProductDto(UUID.randomUUID(), "Test Product", "Desc", BigDecimal.TEN, 10, null, null, null, 0.0, 0);
        CartItem cartItem = mock(CartItem.class);
        when(cartItem.getProduct()).thenReturn(productDto); // Return DTO when getProduct() is called
        when(cartItem.getQuantity()).thenReturn(1);
        when(cartItem.getTotalPrice()).thenReturn(BigDecimal.TEN);

        when(cart.getItems()).thenReturn(List.of(cartItem));
        when(cart.getTotalAmount()).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/form"))
                .andExpect(model().attributeExists("checkoutRequest"))
                .andExpect(model().attributeExists("cart"));
    }

    @Test
    void placeOrder_ShouldRedirectToSuccess_WhenValid() throws Exception {
        mockMvc.perform(post("/checkout")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("street", "Street")
                        .param("city", "City")
                        .param("zipCode", "00-000") // Matches pattern if simple, assuming simple validation
                        .param("email", "john@example.com")
                        .param("paymentMethod", "CARD")
                        .param("deliveryMethod", "COURIER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/success"));

        verify(orderService).placeOrder(any(Cart.class), any(CheckoutRequest.class));
    }

    @Test
    void placeOrder_ShouldReturnForm_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/checkout")
                        .param("firstName", "")) // Invalid
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/form"))
                .andExpect(model().attributeHasErrors("checkoutRequest"));
    }

    @Test
    void placeOrder_ShouldReturnForm_WhenException() throws Exception {
        doThrow(new RuntimeException("Order Error")).when(orderService).placeOrder(any(), any());

        mockMvc.perform(post("/checkout")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("street", "Street")
                        .param("city", "City")
                        .param("zipCode", "00-000")
                        .param("email", "john@example.com")
                        .param("paymentMethod", "CARD")
                        .param("deliveryMethod", "COURIER"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/form"))
                .andExpect(model().attribute("error", "Order Error"));
    }
}
