package com.project6.ecommerce.domain.entity.Cart;

import com.project6.ecommerce.domain.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private ProductDto productDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        productId = UUID.randomUUID();
        productDto = new ProductDto(productId, "Product", "Desc", BigDecimal.TEN, 10, null, null, null, 0.0, 0);
    }

    @Test
    void addItem_ShouldAddProduct_WhenStockAvailable() {
        // Act
        cart.addItem(productDto, 2);

        // Assert
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(20), cart.getTotalAmount());
    }

    @Test
    void addItem_ShouldCapAtStock_WhenQuantityExceeds() {
        // Act
        cart.addItem(productDto, 15); // Stock is 10

        // Assert
        assertEquals(1, cart.getItems().size());
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_ShouldIncreaseQuantity_WhenAlreadyInCart() {
        // Arrange
        cart.addItem(productDto, 2);

        // Act
        cart.addItem(productDto, 3);

        // Assert
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void updateQuantity_ShouldUpdate_WhenValid() {
        // Arrange
        cart.addItem(productDto, 1);

        // Act
        cart.updateQuantity(productId, 5);

        // Assert
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void updateQuantity_ShouldCapAtStock() {
        // Arrange
        cart.addItem(productDto, 1);

        // Act
        cart.updateQuantity(productId, 50); // Stock 10

        // Assert
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }

    @Test
    void updateQuantity_ShouldRemoveExample_WhenZero() {
        // Arrange
        cart.addItem(productDto, 1);

        // Act
        cart.updateQuantity(productId, 0);

        // Assert
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void removeItem_ShouldRemove() {
        // Arrange
        cart.addItem(productDto, 1);

        // Act
        cart.removeItem(productId);

        // Assert
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void clear_ShouldRemoveAll() {
        // Arrange
        cart.addItem(productDto, 1);
        
        // Act
        cart.clear();

        // Assert
        assertTrue(cart.getItems().isEmpty());
    }
}
