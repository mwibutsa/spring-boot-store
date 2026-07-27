package com.mwibutsa.store.controllers;


import com.mwibutsa.store.dto.CheckoutRequest;
import com.mwibutsa.store.dto.CheckoutResponse;
import com.mwibutsa.store.entities.Order;
import com.mwibutsa.store.entities.OrderItem;
import com.mwibutsa.store.entities.OrderStatus;
import com.mwibutsa.store.exceptions.CartNotFoundException;
import com.mwibutsa.store.repositories.CartRepository;
import com.mwibutsa.store.repositories.OrderRepository;
import com.mwibutsa.store.services.AuthService;
import com.mwibutsa.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final CartService cartService;

    @PostMapping
    ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest payload
    ) {
        var cart = cartRepository.findById(payload.getCardId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var items = cart.getItems();

        if (items.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        var order = new Order();
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(authService.getCurrentUser());

        items.forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setUnitPrice(item.getProduct().getPrice());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProduct(item.getProduct());

            order.getItems().add(orderItem);
        });
        
        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return ResponseEntity.ok(new CheckoutResponse(order.getId()));
    }


}
