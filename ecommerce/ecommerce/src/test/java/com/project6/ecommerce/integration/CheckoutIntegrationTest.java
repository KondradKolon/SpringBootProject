package com.project6.ecommerce.integration;

import com.project6.ecommerce.domain.entity.Product.status;
import com.project6.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CheckoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.project6.ecommerce.repository.ProductRepository productRepository;

    private UUID savedProductId;

    @BeforeEach
    void setup() {
        // Create and SAVE a real product
        com.project6.ecommerce.domain.entity.Product.Product product = new com.project6.ecommerce.domain.entity.Product.Product();
        product.setName("Test Product");
        product.setDescription("Desc");
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(10);
        product.setStatus(status.AVAILABLE);
        product = productRepository.save(product);
        savedProductId = product.getId();
    }

    @Test
    void shouldFailValidation_WhenRequiredFieldsMissing() throws Exception {
        // Given: Empty form data
        mockMvc.perform(post("/checkout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "")
                        .param("email", "invalid-email")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/form"))
                .andExpect(model().attributeHasFieldErrors("checkoutRequest", "firstName"))
                .andExpect(model().attributeHasFieldErrors("checkoutRequest", "email"))
                .andExpect(model().attributeHasFieldErrors("checkoutRequest", "paymentMethod"));

        assertEquals(0, orderRepository.count());
    }

    @Test
    void shouldCreateOrder_WhenDataIsValid() throws Exception {
        long initialCount = orderRepository.count();

        // 1. Add item to cart via Controller (to populate Session)
        MvcResult addCartResult = mockMvc.perform(post("/cart/add")
                        .with(csrf())
                        .param("productId", savedProductId.toString())
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) addCartResult.getRequest().getSession();

        // 2. Checkout using the SAME session
        mockMvc.perform(post("/checkout")
                        .with(csrf())
                        .session(session) // <--- CRITICAL: Pass the session with the cart
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("street", "Test St. 1")
                        .param("city", "Warsaw")
                        .param("zipCode", "00-123")
                        .param("paymentMethod", "CARD")
                        .param("deliveryMethod", "COURIER")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/success"));

        // Then: Order created in DB
        assertEquals(initialCount + 1, orderRepository.count());
    }
}
