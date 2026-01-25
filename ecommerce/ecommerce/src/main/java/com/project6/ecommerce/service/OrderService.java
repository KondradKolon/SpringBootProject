package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.dto.CheckoutRequest;
import com.project6.ecommerce.domain.entity.Cart.Cart;
import com.project6.ecommerce.domain.entity.Order.Order;

public interface OrderService {
    Order placeOrder(Cart cart, CheckoutRequest request);
}
