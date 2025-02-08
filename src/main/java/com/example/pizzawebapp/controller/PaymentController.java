package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * Creates a payment intent for the given amount.
     *
     * @param amount The payment amount in dollars.
     * @return The client secret for the payment intent or an error message.
     */
    @PostMapping("/create-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestParam double amount) {
        if (amount <= 0) {
            logger.warn("Invalid amount provided: {}", amount);
            return ResponseEntity.badRequest().body("Amount must be greater than zero.");
        }

        try {
            logger.info("Creating payment intent for amount: {}", amount);
            String clientSecret = paymentService.createPaymentIntent(amount);
            return ResponseEntity.ok(clientSecret);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }
}