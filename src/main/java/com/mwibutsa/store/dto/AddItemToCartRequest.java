package com.mwibutsa.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequest {
    @NotNull(message = "Cart it can not be null")
    private Long productId;
}
