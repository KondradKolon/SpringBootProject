package com.project6.ecommerce.controller;

import com.project6.ecommerce.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // UPDATED
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // UPDATED
import org.springframework.test.context.bean.override.mockito.MockitoBean; // UPDATED
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // UPDATED
    private ReviewService reviewService;

    @Test
    void deleteReview_ShouldRedirect() throws Exception {
        // Arrange
        UUID reviewId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/admin/reviews/delete/{id}", reviewId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")); // Default redirect if no referer

        verify(reviewService).deleteReview(reviewId);
    }
}
