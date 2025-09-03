package com.example.pizzawebapp.dto;

import com.example.pizzawebapp.entity.Cart;
import com.example.pizzawebapp.entity.CartItem;

import java.util.List;
import java.util.stream.Collectors;

public class CartDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private Double totalPrice;

    public CartDTO() {}

    public CartDTO(Long cartId, List<CartItemDTO> items, Double totalPrice) {
        this.cartId = cartId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static CartDTO fromEntity(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList());

        double totalPrice = cart.getItems().stream()
                .map(item -> item.getPizza().getPrice().doubleValue() * item.getQuantity())
                .reduce(0.0, Double::sum);

        return new CartDTO(cart.getId(), itemDTOs, totalPrice);
    }
}
