package com.example.pizzawebapp.dto;

import com.example.pizzawebapp.entity.CartItem;

import java.math.BigDecimal;

public class CartItemDTO {
    private Long pizzaId;
    private String pizzaName;
    private int quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;

    public CartItemDTO() {}

    public CartItemDTO(Long pizzaId, String pizzaName, int quantity, BigDecimal pricePerUnit, BigDecimal totalPrice) {
        this.pizzaId = pizzaId;
        this.pizzaName = pizzaName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
    }

    public Long getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(Long pizzaId) {
        this.pizzaId = pizzaId;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static CartItemDTO fromEntity(CartItem item) {
        return new CartItemDTO(
                item.getPizza().getId(),
                item.getPizza().getName(),
                item.getQuantity(),
                item.getPizza().getPrice(),
                item.getTotalPrice()
        );
    }
}
