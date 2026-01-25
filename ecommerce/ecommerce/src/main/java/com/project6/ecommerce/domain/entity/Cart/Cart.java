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

        int newTotal = currentInCart + quantity;

        if (newTotal > stock) {
            newTotal = stock;
        }

        if (newTotal <= 0) return; 

        if (items.containsKey(product.id())) {
            items.get(product.id()).setQuantity(newTotal);
        } else {
            CartItem item = new CartItem(product, newTotal);
            items.put(product.id(), item);
        }
    }

    public void updateQuantity(UUID productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            int stock = item.getProduct().quantity();

            if (quantity > stock) {
                quantity = stock; //jak wiecej niz na magazynie to zamawiamy tyle co jest
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