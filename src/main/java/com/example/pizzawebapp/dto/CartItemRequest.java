package com.example.pizzawebapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {
    @NotNull(message = "Pizza ID cannot be null")
    private Long pizzaId;

    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;

    // Getters and Setters
    public Long getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(Long pizzaId) {
        this.pizzaId = pizzaId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}