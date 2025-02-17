package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.PromoCode;
import com.example.pizzawebapp.repository.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PromoCodeService {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    /**
     * Apply a promo code to the cart total.
     */
    public double applyPromoCode(String code, double cartTotal) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code: " + code));

        if (!promoCode.isActive()) {
            throw new IllegalArgumentException("Promo code is inactive: " + code);
        }

        if (cartTotal < promoCode.getMinOrderAmount()) {
            throw new IllegalArgumentException("Cart total does not meet the minimum order amount required for this promo code.");
        }

        // Calculate the discounted total
        double discountAmount = (promoCode.getDiscountPercentage() / 100) * cartTotal;
        double discountedTotal = cartTotal - discountAmount;

        // Ensure the discounted total is not negative
        return Math.max(discountedTotal, 0);
    }

    /**
     * Create a new promo code.
     */
    public PromoCode createPromoCode(String code, double discountPercentage, double minOrderAmount) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(code);
        promoCode.setDiscountPercentage(discountPercentage);
        promoCode.setMinOrderAmount(minOrderAmount);
        promoCode.setActive(true); // By default, promo codes are active
        return promoCodeRepository.save(promoCode);
    }

    /**
     * Update an existing promo code.
     */
    public void updatePromoCode(String code, Double discountPercentage, Double minOrderAmount) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promo code not found: " + code));

        if (discountPercentage != null) {
            promoCode.setDiscountPercentage(discountPercentage);
        }
        if (minOrderAmount != null) {
            promoCode.setMinOrderAmount(minOrderAmount);
        }

        promoCodeRepository.save(promoCode);
    }

    /**
     * Deactivate a promo code.
     */
    public void deactivatePromoCode(String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Promo code not found: " + code));

        promoCode.setActive(false); // Deactivate the promo code
        promoCodeRepository.save(promoCode);
    }
}