package com.mwibutsa.store.services;

import com.mwibutsa.store.dto.CheckoutRequest;
import com.mwibutsa.store.dto.CheckoutResponse;
import com.mwibutsa.store.entities.Order;
import com.mwibutsa.store.exceptions.CartEmptyException;
import com.mwibutsa.store.exceptions.CartNotFoundException;
import com.mwibutsa.store.exceptions.PaymentException;
import com.mwibutsa.store.repositories.CartRepository;
import com.mwibutsa.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest payload) {
        var cart = cartRepository.findById(payload.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var items = cart.getItems();
        if (items.isEmpty()) {
            throw new CartEmptyException();
        }
        var order = Order.fromCart(cart, authService.getCurrentUser());
        orderRepository.save(order);

        // create a checkout session.
        try {
            var checkoutSession = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cart.getId());
            return new CheckoutResponse(order.getId(), checkoutSession.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }

    }
}
