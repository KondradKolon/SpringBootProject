package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final Cart cart;
    private final ProductService productService;

    // 1. Wyświetlanie koszyka
    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cart", cart);
        return "cart/view";
    }

    // 2. Dodawanie produktu
    @PostMapping("/add")
    public String addToCart(@RequestParam UUID productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestHeader(value = "Referer", required = false) String referer) {
        // Pobieramy produkt (DTO)
        ProductDto product = productService.getProductById(productId);

        // Delegujemy logikę do obiektu Cart
        cart.addItem(product, quantity);

        // Wracamy tam, skąd przyszedł użytkownik (np. na stronę produktu)
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    // 3. Aktualizacja ilości
    @PostMapping("/update")
    public String updateQuantity(@RequestParam UUID productId, @RequestParam int quantity) {
        cart.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    // 4. Usuwanie pozycji
    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable UUID id) {
        cart.removeItem(id);
        return "redirect:/cart";
    }
}