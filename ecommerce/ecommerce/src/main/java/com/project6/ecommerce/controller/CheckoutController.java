package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final Cart cart;
    private final OrderService orderService;

    @GetMapping
    public String showCheckout(Model model) {
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("checkoutRequest", new CheckoutRequest(null, null, null, null, null, null, null, null));
        model.addAttribute("cart", cart);
        return "checkout/form";
    }

    @PostMapping
    public String placeOrder(@Valid @ModelAttribute CheckoutRequest checkoutRequest, 
                             BindingResult bindingResult, 
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout/form";
        }
        
        try {
            orderService.placeOrder(cart, checkoutRequest);
            return "redirect:/checkout/success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cart", cart);
            return "checkout/form";
        }
    }
    
    @GetMapping("/success")
    public String success() {
        return "checkout/success";
    }
}
