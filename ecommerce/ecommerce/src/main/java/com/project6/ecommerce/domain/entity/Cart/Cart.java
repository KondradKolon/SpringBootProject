package com.project6.ecommerce.domain.entity.Cart;

import com.project6.ecommerce.domain.dto.ProductDto;
import com.project6.ecommerce.domain.entity.Cart.CartItem;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@SessionScope
public class Cart {

    private final Map<UUID, CartItem> items = new HashMap<>();

    public void addItem(ProductDto product, int quantity) {
        int stock = product.quantity() != null ? product.quantity() : 0;
        int currentInCart = items.containsKey(product.id()) ? items.get(product.id()).getQuantity() : 0;

        // Obliczamy ile jeszcze możemy dodać
        int newTotal = currentInCart + quantity;

        // WALIDACJA 1: Nie możemy przekroczyć stanu magazynowego
        if (newTotal > stock) {
            // Opcja bezpieczna: dodajemy tyle, ile się da (do maxa)
            newTotal = stock;
        }

        if (newTotal <= 0) return; // Jeśli nie ma towaru, nic nie robimy

        if (items.containsKey(product.id())) {
            items.get(product.id()).setQuantity(newTotal);
        } else {
            // Tworzymy nowy item z wyliczoną bezpieczną ilością (tutaj quantity musimy dopasować)
            // Ponieważ CartItem trzyma absolutną wartość, a my tu dodajemy, prościej jest stworzyć item i ustawić mu quantity
            CartItem item = new CartItem(product, newTotal);
            items.put(product.id(), item);
        }
    }

    public void updateQuantity(UUID productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            int stock = item.getProduct().quantity();

            // WALIDACJA 2: Sprawdzamy czy wpisana liczba (np. 50) nie jest większa niż stan (np. 10)
            if (quantity > stock) {
                quantity = stock; // "Hard cap" - ustawiamy max dostępny
            }

            if (quantity <= 0) {
                items.remove(productId);
            } else {
                item.setQuantity(quantity);
            }
        }
    }

    public void removeItem(UUID productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public BigDecimal getTotalAmount() {
        return items.values().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemsCount() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}