package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.Cart;
import com.example.pizzawebapp.entity.CartItem;
import com.example.pizzawebapp.entity.Pizza;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.CartRepository;
import com.example.pizzawebapp.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    /**
     * Get the cart details for the authenticated user.
     */
    public Map<String, Object> getCartByUser(User user) {
        Cart cart = getOrCreateCart(user);
        Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("cartId", cart.getId());
        cartMap.put("items", cart.getItems());
        cartMap.put("totalPrice", cart.getTotalPrice());
        return cartMap;
    }

    /**
     * Add an item to the cart.
     */
    public void addItemToCart(User user, Long pizzaId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza not found"));
        Cart cart = getOrCreateCart(user);
        cart.getItems().stream()
                .filter(item -> item.getPizza().getId().equals(pizzaId))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setPizza(pizza);
                            newItem.setQuantity(quantity);
                            cart.addItem(newItem);
                        }
                );
        cartRepository.save(cart);
    }

    /**
     * Remove an item from the cart.
     */
    public void removeItemFromCart(User user, Long pizzaId) {
        Cart cart = getOrCreateCart(user);
        boolean removed = cart.getItems().removeIf(item -> item.getPizza().getId().equals(pizzaId));
        if (removed) {
            cartRepository.save(cart);
        }
    }

    /**
     * Calculate the total amount of the cart.
     */
    public double calculateTotalAmount(User user) {
        Cart cart = getOrCreateCart(user);
        return cart.getItems().stream()
                .mapToDouble(item -> item.getPizza().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .doubleValue())
                .sum();
    }

    /**
     * Clear the cart after an order is placed.
     */
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    /**
     * Get or create a cart for the user.
     */
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
    }

    /**
     * Create a new cart for the user.
     */
    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}