package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import com.project6.ecommerce.domain.entity.Order.Order;
import com.project6.ecommerce.domain.entity.Order.OrderItem;
import com.project6.ecommerce.domain.entity.Order.status;
import com.project6.ecommerce.domain.entity.Product.Product;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.repository.OrderRepository;
import com.project6.ecommerce.repository.ProductRepository;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional // cała metoda w jednej transakcji - jak coś padnie to nic się nie zapisze
    public Order placeOrder(Cart cart, CheckoutRequest request) {
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("koszyk jest pusty");
        }

        Order order = new Order();
        
        // przypisz zamówienie do użytkownika jeśli jest zalogowany
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            userRepository.findByEmail(auth.getName()).ifPresent(order::setUser);
        }

        order.setFirstName(request.firstName());
        order.setLastName(request.lastName());
        order.setStreet(request.street());
        order.setCity(request.city());
        order.setZipCode(request.zipCode());
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus(status.NEW);
        order.setCreatedAt(LocalDateTime.now());

        // konwersja pozycji koszyka na pozycje zamówienia
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().id())
                    .orElseThrow(() -> new RuntimeException("produkt nie istnieje"));
            
            // sprawdzenie czy mamy tyle towaru na stanie
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("za mało towaru na stanie: " + product.getName());
            }
            // aktualizacja stanu magazynowego
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(product, cartItem.getQuantity(), cartItem.getPrice());
            order.addItem(orderItem);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // czyszczenie koszyka po udanym zakupie
        cart.clear();
        
        return savedOrder;
    }
}
