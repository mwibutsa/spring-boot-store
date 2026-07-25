package com.mwibutsa.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @Positive
    @Min(value = 1, message = "Quantity should be greater than 0")
    private Integer quantity;
}

