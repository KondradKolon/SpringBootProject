package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import com.project6.ecommerce.domain.entity.Order.Order;
import com.project6.ecommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutRestController.class)
@AutoConfigureMockMvc(addFilters = false) 
class CheckoutRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private Cart cart;

    @MockitoBean
    private OrderService orderService;

    @Test
    void placeOrder_ShouldReturnBadRequest_WhenCartIsEmpty() throws Exception {
        when(cart.getItems()).thenReturn(Collections.emptyList());

        CheckoutRequest request = new CheckoutRequest("John", "Doe", "Street", "City", "00-000", "john@example.com", "CARD", "COURIER");

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cart").value("Koszyk jest pusty"));
    }

    @Test
    void placeOrder_ShouldReturnCreated_WhenValid() throws Exception {
        when(cart.getItems()).thenReturn(List.of(mock(CartItem.class)));
        
        Order mockOrder = new Order();
        mockOrder.setId(UUID.randomUUID());
        when(orderService.placeOrder(any(Cart.class), any(CheckoutRequest.class))).thenReturn(mockOrder);

        CheckoutRequest request = new CheckoutRequest("John", "Doe", "Street", "City", "00-000", "john@example.com", "CARD", "COURIER");

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Zamówienie złożone"))
                .andExpect(jsonPath("$.orderId").isNotEmpty());

        verify(orderService).placeOrder(any(Cart.class), any(CheckoutRequest.class));
    }

    @Test
    void placeOrder_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        CheckoutRequest invalidRequest = new CheckoutRequest("", "", "", "", "INVALID", "INVALID", "", "");

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.zipCode").exists());
        
        verify(orderService, never()).placeOrder(any(), any());
    }
}
