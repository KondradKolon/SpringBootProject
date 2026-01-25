package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.Review;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.ReviewRepository;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product product;
    private User user;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);

        user = new User();
        user.setEmail("user@example.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addReview_ShouldSaveReview_WhenValid() {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest(5, "Great product!");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Mock Auth
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getName()).thenReturn("user@example.com");
        
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Review result = reviewService.addReview(productId, request);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getContent());
        assertEquals(product, result.getProduct());
        assertEquals(user, result.getUser());
        verify(reviewRepository).save(any(Review.class));
    }
    
    @Test
    void addReview_ShouldThrow_WhenProductNotFound() {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest(5, "Great!");
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(productId, request));
        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void addReview_ShouldThrow_WhenUserNotFound() {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest(5, "Great!");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Mock Auth
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getName()).thenReturn("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewService.addReview(productId, request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getReviews_ShouldReturnList() {
        // Arrange
        when(reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId)).thenReturn(List.of(new Review()));

        // Act
        List<Review> result = reviewService.getReviews(productId);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void deleteReview_ShouldCallRepository() {
        // Arrange
        UUID reviewId = UUID.randomUUID();

        // Act
        reviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository).deleteById(reviewId);
    }
}
