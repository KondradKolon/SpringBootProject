package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Zarządzanie opiniami (REST)")
public class ReviewRestController {

    private final ReviewService reviewService;

    @Operation(summary = "Dodaj opinię", description = "Dodaje nową opinię do produktu.")
    @PostMapping
    public ResponseEntity<?> addReview(
            @Parameter(description = "ID produktu") @RequestParam UUID productId,
            @Valid @RequestBody CreateReviewRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            reviewService.addReview(productId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Opinia została dodana"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
