package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateReviewRequest;
import com.project6.ecommerce.domain.dto.ProductDto; // Twój DTO
import com.project6.ecommerce.service.ProductService;

import com.project6.ecommerce.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ReviewService reviewService;
    private final ProductService productService;

    // STRONA GŁÓWNA
    @GetMapping("/")
    public String home(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size) {

        // Pobieramy stronę Twoich ProductDto
        Page<ProductDto> productPage = productService.getAllProducts(PageRequest.of(page, size));

        model.addAttribute("products", productPage);
        return "index"; // Szuka pliku templates/index.html
    }

    // SZCZEGÓŁY PRODUKTU
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable UUID id, Model model) {
        ProductDto product = productService.getProductById(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.getReviews(id));
        return "product-details"; // Szuka pliku templates/product-details.html
    }

    // Endpoint do dodawania opinii (POST)
    @PostMapping("/product/{id}/reviews")
    public String addReview(@PathVariable UUID id,
                            @ModelAttribute CreateReviewRequest request,
                            Principal principal) { // Principal to zalogowany user

        // Jeśli user nie jest zalogowany, Principal będzie null (ale Spring Security i tak go pewnie wytnie wcześniej)
        if (principal == null) {
            return "redirect:/login";
        }

        reviewService.addReview(id, request); // Service gets user from SecurityContext

        return "redirect:/product/" + id; // Wracamy do szczegółów produktu
    }
}