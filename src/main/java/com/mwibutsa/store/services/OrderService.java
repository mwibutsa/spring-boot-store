package com.mwibutsa.store.services;

import com.mwibutsa.store.dto.OrderDto;
import com.mwibutsa.store.exceptions.OrderNotFoundException;
import com.mwibutsa.store.mappers.OrderMapper;
import com.mwibutsa.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private AuthService authService;
    private OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        return orderRepository.
                getAllByCustomer(user)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    public OrderDto getOrder(Long orderId) {
        var user = authService.getCurrentUser();
        var order = orderRepository.getOrderWithItems(orderId).orElseThrow(OrderNotFoundException::new);
        
        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You don't have access to this order");
        }
        return orderMapper.toDto(order);
    }
}
