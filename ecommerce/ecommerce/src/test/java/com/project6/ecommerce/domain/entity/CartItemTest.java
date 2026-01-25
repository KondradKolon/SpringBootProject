package com.project6.ecommerce.domain.entity;

import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartItemTest {

    @Test
    void getTotalPrice_ShouldCalculateCorrectly() {
        // Arrange
        ProductDto product = new ProductDto(UUID.randomUUID(), "P", "D", BigDecimal.valueOf(10.0), 10, null, null, null, 0.0, 0);
        CartItem item = new CartItem(product, 5); // 5 * 10 = 50

        // Act
        BigDecimal total = item.getTotalPrice();

        // Assert
        assertEquals(BigDecimal.valueOf(50.0), total);
    }

    @Test
    void getPrice_ShouldReturnProductPrice() {
        ProductDto product = new ProductDto(UUID.randomUUID(), "P", "D", BigDecimal.valueOf(12.34), 10, null, null, null, 0.0, 0);
        CartItem item = new CartItem(product, 1);
        
        assertEquals(BigDecimal.valueOf(12.34), item.getPrice());
    }
}
