package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.dto.CartDTO;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.CartService;
import com.example.pizzawebapp.service.PromoCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private PromoCodeService promoCodeService;

    /**
     * Get the cart for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(@RequestAttribute("user") User user) {
        try {
            Map<String, Object> cartDetails = cartService.getCartByUser(user);
            return ResponseEntity.ok(cartDetails);
        } catch (Exception e) {
            logger.error("Error fetching cart for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Add an item to the cart.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(
            @RequestAttribute("user") User user,
            @RequestParam Long pizzaId,
            @RequestParam(defaultValue = "1") int quantity) {
        if (quantity <= 0) {
            logger.warn("Invalid quantity provided: {}", quantity);
            return ResponseEntity.badRequest().body("Quantity must be greater than zero.");
        }
        try {
            cartService.addItemToCart(user, pizzaId, quantity);
            logger.info("Item added to cart for user {}: Pizza ID {}, Quantity {}", user.getUsername(), pizzaId, quantity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error adding item to cart for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the item to the cart.");
        }
    }

    /**
     * Remove an item from the cart.
     */
    @DeleteMapping("/remove/{pizzaId}")
    public ResponseEntity<?> removeItemFromCart(
            @RequestAttribute("user") User user,
            @PathVariable Long pizzaId) {
        try {
            cartService.removeItemFromCart(user, pizzaId);
            logger.info("Item removed from cart for user {}: Pizza ID {}", user.getUsername(), pizzaId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error removing item from cart for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the item from the cart.");
        }
    }

    /**
     * Apply a promo code to the cart total.
     */
    @PostMapping("/apply-promo")
    public ResponseEntity<?> applyPromoCode(
            @RequestAttribute("user") User user,
            @RequestParam String code) {
        try {
            double cartTotal = cartService.calculateTotalAmount(user); // Calculate the current cart total
            double discountedTotal = promoCodeService.applyPromoCode(code, cartTotal); // Apply the promo code
            Map<String, Object> response = new HashMap<>();
            response.put("originalTotal", cartTotal);
            response.put("discountedTotal", discountedTotal);
            logger.info("Promo code applied successfully for user {}: Original Total {}, Discounted Total {}",
                    user.getUsername(), cartTotal, discountedTotal);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid promo code used by user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error applying promo code for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while applying the promo code."));
        }
    }
}