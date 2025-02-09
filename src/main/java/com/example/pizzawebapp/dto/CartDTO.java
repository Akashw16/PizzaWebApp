package com.example.pizzawebapp.dto;

import com.example.pizzawebapp.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
    private Long cartId;
    private List<CartItem> items;
    private BigDecimal totalPrice;

    // Constructors
    public CartDTO() {}

    public CartDTO(Long cartId, List<CartItem> items, BigDecimal totalPrice) {
        this.cartId = cartId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}