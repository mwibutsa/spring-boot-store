package com.mwibutsa.store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long id;
    private Integer quantity;
    private BigDecimal totalPrice;
    private CartProductDto product;
}
