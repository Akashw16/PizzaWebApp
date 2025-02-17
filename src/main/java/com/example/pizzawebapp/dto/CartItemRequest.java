package com.example.pizzawebapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull(message = "Pizza ID cannot be null")
    private Long pizzaId;

    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;
}
