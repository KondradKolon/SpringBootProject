package com.project6.ecommerce.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CheckoutRequest(
        @NotBlank(message = "First name is required")
        String firstName,
        
        @NotBlank(message = "Last name is required")
        String lastName,
        
        @NotBlank(message = "Street is required")
        String street,
        
        @NotBlank(message = "City is required")
        String city,
        
        @NotBlank(message = "Zip code is required")
        @Pattern(regexp = "\\d{2}-\\d{3}", message = "Invalid zip code format (XX-XXX)")
        String zipCode,

        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format (Regex)")
        String email,

        @NotBlank(message = "Payment method is required")
        String paymentMethod,
        
        @NotBlank(message = "Delivery method is required")
        String deliveryMethod
) {}
