package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/promo")
public class AdminController {

    @Autowired
    private PromoCodeService promoCodeService;

    /**
     * Create a new promo code.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPromoCode(
            @RequestParam String code,
            @RequestParam double discountPercentage,
            @RequestParam double minOrderAmount) {
        try {
            promoCodeService.createPromoCode(code, discountPercentage, minOrderAmount);
            return ResponseEntity.ok("Promo code created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create promo code: " + e.getMessage());
        }
    }

    /**
     * Update an existing promo code.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updatePromoCode(
            @RequestParam String code,
            @RequestParam(required = false) Double discountPercentage,
            @RequestParam(required = false) Double minOrderAmount) {
        try {
            promoCodeService.updatePromoCode(code, discountPercentage, minOrderAmount);
            return ResponseEntity.ok("Promo code updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update promo code: " + e.getMessage());
        }
    }

    /**
     * Deactivate a promo code.
     */
    @DeleteMapping("/deactivate")
    public ResponseEntity<?> deactivatePromoCode(@RequestParam String code) {
        try {
            promoCodeService.deactivatePromoCode(code);
            return ResponseEntity.ok("Promo code deactivated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to deactivate promo code: " + e.getMessage());
        }
    }
}