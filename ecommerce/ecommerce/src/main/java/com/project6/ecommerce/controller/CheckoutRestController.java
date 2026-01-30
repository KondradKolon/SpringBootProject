package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Order.Order;
import com.project6.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Zarządzanie procesem zamówienia (REST)")
public class CheckoutRestController {

    private final Cart cart;
    private final OrderService orderService;

    @Operation(summary = "Złóż zamówienie", description = "Waliduje dane i składa zamówienie dla bieżącego koszyka.")
    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody CheckoutRequest checkoutRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }
        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("cart", "Koszyk jest pusty"));
        }

        try {
            Order order = orderService.placeOrder(cart, checkoutRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Zamówienie złożone", "orderId", order.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
