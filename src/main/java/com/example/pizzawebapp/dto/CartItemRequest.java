package com.example.pizzawebapp.dto;

public class CartItemRequest {
    private Long pizzaId;
    private int quantity;

    public CartItemRequest() {}

    public CartItemRequest(Long pizzaId, int quantity) {
        this.pizzaId = pizzaId;
        this.quantity = quantity;
    }

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
