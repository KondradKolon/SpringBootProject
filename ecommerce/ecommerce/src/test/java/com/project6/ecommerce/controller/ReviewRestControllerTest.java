package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void addReview_ShouldReturnCreated_WhenValid() throws Exception {
        UUID productId = UUID.randomUUID();
        CreateReviewRequest request = new CreateReviewRequest(5, "Great product!");

        mockMvc.perform(post("/api/v1/reviews")
                        .param("productId", productId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Opinia została dodana"));

        verify(reviewService).addReview(eq(productId), any(CreateReviewRequest.class));
    }

    @Test
    void addReview_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        UUID productId = UUID.randomUUID();
        CreateReviewRequest request = new CreateReviewRequest(6, "Bad"); // Invalid rating (6) and length (<5)

        mockMvc.perform(post("/api/v1/reviews")
                        .param("productId", productId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.content").exists());
    }
}
