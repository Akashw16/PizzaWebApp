package com.example.pizzawebapp.service;

import com.example.pizzawebapp.dto.CartDTO;
import com.example.pizzawebapp.dto.PromoResponseDTO;
import com.example.pizzawebapp.entity.Cart;
import com.example.pizzawebapp.entity.CartItem;
import com.example.pizzawebapp.entity.Pizza;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.CartRepository;
import com.example.pizzawebapp.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    public CartDTO getCartByUser(User user) {
        Cart cart = getOrCreateCart(user);
        List<CartItem> cartItems = cart.getItems();
        double totalPrice = calculateTotal(cartItems);
        return CartDTO.fromEntity(cart);

    }

    public void addItemToCart(User user, Long pizzaId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza not found"));

        Cart cart = getOrCreateCart(user);

        // Check if the item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getPizza().getId().equals(pizzaId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setPizza(pizza);
            newItem.setQuantity(quantity);
            newItem.setCart(cart); // IMPORTANT
            cart.getItems().add(newItem); // This triggers cascade save
        }

        cartRepository.save(cart); // This will also save CartItem because of CascadeType.ALL
    }

    public void removeItemFromCart(User user, Long pizzaId) {
        Cart cart = getOrCreateCart(user);
        boolean removed = cart.getItems().removeIf(item -> item.getPizza().getId().equals(pizzaId));
        if (removed) {
            cartRepository.save(cart);
        }
    }

    public double calculateTotalAmount(User user) {
        Cart cart = getOrCreateCart(user);
        return calculateTotal(cart.getItems());
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public PromoResponseDTO applyPromoCode(String code, double originalPrice) {
        double discount = 0.0;
        if (code.equalsIgnoreCase("PIZZA20")) {
            discount = 0.2 * originalPrice;
        }

        double discountedPrice = originalPrice - discount;
        return new PromoResponseDTO(originalPrice, discountedPrice, "Promo applied successfully");
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private double calculateTotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPizza().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .doubleValue())
                .sum();
    }
}
