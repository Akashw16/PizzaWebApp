package com.example.pizzawebapp.dto;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long id;
    private PizzaDTO pizza;
    private Integer quantity;
    private BigDecimal totalPrice;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PizzaDTO getPizza() {
        return pizza;
    }

    public void setPizza(PizzaDTO pizza) {
        this.pizza = pizza;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}