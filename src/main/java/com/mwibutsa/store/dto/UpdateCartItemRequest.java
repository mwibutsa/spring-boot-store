package com.mwibutsa.store.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @Positive
    private Integer quantity;
}

