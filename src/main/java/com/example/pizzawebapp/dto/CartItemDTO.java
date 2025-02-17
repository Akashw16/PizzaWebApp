package com.example.pizzawebapp.dto;

import com.example.pizzawebapp.entity.CartItem;
import com.example.pizzawebapp.entity.Pizza;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString automatically
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Pizza pizza;
    private Integer quantity;

    // Manually added setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Converts a CartItem entity into a CartItemDTO.
     */
    public static CartItemDTO fromEntity(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItem.getId());
        cartItemDTO.setPizza(cartItem.getPizza());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        return cartItemDTO;
    }
}