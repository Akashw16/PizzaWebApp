package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.dto.CartDTO;
import com.example.pizzawebapp.dto.PromoResponseDTO;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.CartService;
import com.example.pizzawebapp.service.PromoCodeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private PromoCodeService promoCodeService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestAttribute("user") User user) {
        try {
            return ResponseEntity.ok(cartService.getCartByUser(user));
        } catch (Exception e) {
            logger.error("Error fetching cart for user {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ✅ FIXED: Use RequestParam instead of ModelAttribute
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestAttribute("user") User user,
            @RequestParam Long pizzaId,
            @RequestParam int quantity) {
        try {
            cartService.addItemToCart(user, pizzaId, quantity);
            logger.info("Added to cart: user={}, pizzaId={}, quantity={}", user.getUsername(), pizzaId, quantity);
            return ResponseEntity.ok().body(
                    java.util.Map.of("message", "Item added to cart successfully"));
        } catch (Exception e) {
            logger.error("Error adding to cart: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "Could not add item to cart"));
        }
    }

    @PostMapping("/apply-promo")
    public ResponseEntity<?> applyPromoCode(
            @RequestAttribute("user") User user,
            @RequestParam String code) {
        try {
            double original = cartService.calculateTotalAmount(user);
            PromoResponseDTO response = cartService.applyPromoCode(code, original);
            logger.info("Promo applied: user={}, original={}, discounted={}",
                    user.getUsername(), original, response.getDiscountedTotal());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid promo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Promo code application failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "An error occurred while applying the promo code."));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(
            @RequestAttribute("user") User user,
            @RequestParam Long pizzaId) {
        try {
            cartService.removeItemFromCart(user, pizzaId);
            logger.info("Removed from cart: user={}, pizzaId={}", user.getUsername(), pizzaId);
            return ResponseEntity.ok().body(
                    java.util.Map.of("message", "Item removed from cart successfully"));
        } catch (Exception e) {
            logger.error("Error removing from cart: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "Could not remove item from cart"));
        }
    }
}
