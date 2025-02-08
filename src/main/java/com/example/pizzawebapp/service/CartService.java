package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.Cart;
import com.example.pizzawebapp.entity.CartItem;
import com.example.pizzawebapp.entity.Pizza;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.CartRepository;
import com.example.pizzawebapp.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user); // Set the user for the cart
        return cartRepository.save(cart); // Save the new cart
    }

    public void addItemToCart(User user, Long pizzaId, int quantity) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza not found"));

        Cart cart = getCartByUser(user);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getPizza().getId().equals(pizzaId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setPizza(pizza); // Set the pizza for the cart item
            newItem.setQuantity(quantity); // Set the quantity for the cart item
            cart.addItem(newItem); // Add the item to the cart
        }

        cartRepository.save(cart); // Save the updated cart
    }

    public void removeItemFromCart(User user, Long pizzaId) {
        Cart cart = getCartByUser(user);
        cart.getItems().removeIf(item -> item.getPizza().getId().equals(pizzaId));
        cartRepository.save(cart); // Save the updated cart
    }
}