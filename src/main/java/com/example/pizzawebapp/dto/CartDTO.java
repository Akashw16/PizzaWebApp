package com.example.pizzawebapp.dto;

import com.example.pizzawebapp.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data // Generates getters, setters, equals, hashCode, and toString automatically
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private List<CartItemDTO> items;
    private Double totalPrice;

    // Manually added setter methods
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Converts a list of CartItem entities and a total price into a CartDTO.
     */
    public static CartDTO fromEntity(List<CartItem> cartItems, Double totalPrice) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(cartItems.stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList()));
        cartDTO.setTotalPrice(totalPrice);
        return cartDTO;
    }
}