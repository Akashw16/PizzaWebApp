package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(cartService.getCartByUser(user));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addItemToCart(
            @RequestAttribute("user") User user,
            @RequestParam Long pizzaId,
            @RequestParam int quantity) {
        cartService.addItemToCart(user, pizzaId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{pizzaId}")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestAttribute("user") User user,
            @PathVariable Long pizzaId) {
        cartService.removeItemFromCart(user, pizzaId);
        return ResponseEntity.noContent().build();
    }
}