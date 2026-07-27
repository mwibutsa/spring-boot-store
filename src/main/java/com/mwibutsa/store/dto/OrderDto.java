package com.mwibutsa.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderDto {
    private UUID id;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private UserDto customer;
}
