package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import com.project6.ecommerce.domain.entity.Order.Order;
import com.project6.ecommerce.domain.entity.Order.status;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.repository.OrderRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private CheckoutRequest checkoutRequest;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        
        UUID productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        product.setQuantity(10); 

        productDto = new ProductDto(productId, "Test Product", "Desc", BigDecimal.TEN, 10, null, null, null, 0.0, 0);
        
        checkoutRequest = new CheckoutRequest("John", "Doe", "Street 1", "City", "00-000", "john@example.com", "CARD", "COURIER");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void placeOrder_ShouldCreateOrder_WhenCartIsNotEmpty() {
        // Arrange
        cart.addItem(productDto, 2);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.placeOrder(cart, checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
        assertEquals(status.NEW, result.getStatus());
        assertEquals(1, result.getItems().size());
        
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void placeOrder_ShouldLinkUser_WhenAuthenticated() {
        // Arrange
        cart.addItem(productDto, 1);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock Security
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("user@example.com");
        
        User user = new User(UUID.randomUUID(), "User", "user@example.com", "pass", role.USER);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        Order result = orderService.placeOrder(cart, checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
    }

    @Test
    void placeOrder_ShouldThrowException_WhenCartIsEmpty() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(cart, checkoutRequest);
        });
        assertEquals("Cart is empty", exception.getMessage());
    }
    
    @Test
    void placeOrder_ShouldThrowException_WhenNotEnoughStock() {
        // Arrange
        cart.addItem(productDto, 11); 
        
        // Override DB product to have explicitly low stock
        product.setQuantity(5);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(cart, checkoutRequest);
        });
        assertTrue(exception.getMessage().contains("Not enough stock"));
    }

    @Test
    void placeOrder_ShouldSucceed_WhenStockIsExact() {
        // Arrange
        product.setQuantity(1); // Stock 1
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        
        cart.addItem(productDto, 1); // Buying 1

        // Act
        Order result = orderService.placeOrder(cart, checkoutRequest);

        // Assert
        assertNotNull(result);
        assertEquals(0, product.getQuantity()); // Stock depleted
        verify(productRepository).save(product);
    }
}
