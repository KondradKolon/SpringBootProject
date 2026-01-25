package com.project6.ecommerce.domain.entity.Cart;

import com.project6.ecommerce.domain.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private final ProductDto product; // Używamy DTO, jest lżejsze niż Entity
    private int quantity;

    public BigDecimal getTotalPrice() {
        if (product.price() == null) return BigDecimal.ZERO;
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getPrice() {
        return product.price();
    }
}