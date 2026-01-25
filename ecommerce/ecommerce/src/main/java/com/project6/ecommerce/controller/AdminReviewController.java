package com.project6.ecommerce.controller;

import com.project6.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable UUID id, @org.springframework.web.bind.annotation.RequestHeader(value = "Referer", required = false) String referer) {
        reviewService.deleteReview(id);
        return "redirect:" + (referer != null ? referer : "/");
    }
}
