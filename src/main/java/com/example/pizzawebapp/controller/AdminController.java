package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.PromoCode;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.PromoCodeService;
import com.example.pizzawebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PromoCodeService promoCodeService;

    /**
     * Register a new admin user.
     */
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody User admin) {
        try {
            // Validate input
            if (admin.getUsername() == null || admin.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required.");
            }
            if (!admin.getUsername().endsWith("@myApp.com")) {
                return ResponseEntity.badRequest().body("Admin registration is only allowed for emails ending with '@myApp.com'.");
            }

            // Set role to ADMIN and create the admin
            admin.setRole("ADMIN");
            userService.createAdmin(admin);
            return ResponseEntity.ok("Admin registered successfully: " + admin.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Create a new promo code.
     */
    @PostMapping("/create-promo-code")
    public ResponseEntity<String> createPromoCode(
            @RequestParam String code,
            @RequestParam double discountPercentage,
            @RequestParam double minOrderAmount) {
        try {
            // Validate input
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("Promo code cannot be empty.");
            }
            if (discountPercentage <= 0 || discountPercentage > 100) {
                return ResponseEntity.badRequest().body("Discount percentage must be between 1 and 100.");
            }
            if (minOrderAmount <= 0) {
                return ResponseEntity.badRequest().body("Minimum order amount must be greater than zero.");
            }

            // Create the promo code
            PromoCode promoCode = promoCodeService.createPromoCode(code, discountPercentage, minOrderAmount);
            return ResponseEntity.ok("Promo code created successfully: " + promoCode.getCode());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Update an existing promo code.
     */
    @PutMapping("/update-promo-code")
    public ResponseEntity<String> updatePromoCode(
            @RequestParam String code,
            @RequestParam(required = false) Double discountPercentage,
            @RequestParam(required = false) Double minOrderAmount) {
        try {
            // Validate input
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("Promo code cannot be empty.");
            }
            if (discountPercentage != null && (discountPercentage <= 0 || discountPercentage > 100)) {
                return ResponseEntity.badRequest().body("Discount percentage must be between 1 and 100.");
            }
            if (minOrderAmount != null && minOrderAmount <= 0) {
                return ResponseEntity.badRequest().body("Minimum order amount must be greater than zero.");
            }

            // Update the promo code
            promoCodeService.updatePromoCode(code, discountPercentage, minOrderAmount);
            return ResponseEntity.ok("Promo code updated successfully: " + code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Deactivate a promo code.
     */
    @DeleteMapping("/deactivate-promo-code")
    public ResponseEntity<String> deactivatePromoCode(@RequestParam String code) {
        try {
            // Validate input
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest().body("Promo code cannot be empty.");
            }

            // Deactivate the promo code
            promoCodeService.deactivatePromoCode(code);
            return ResponseEntity.ok("Promo code deactivated successfully: " + code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}