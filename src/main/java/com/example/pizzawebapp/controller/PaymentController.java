package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.service.CartService;
import com.example.pizzawebapp.service.PaymentService;
import com.example.pizzawebapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartService cartService; // Inject CartService

    /**
     * Processes a payment using the specified method and amount.
     *
     * @param request The payment request containing payment method and amount.
     * @param user    The authenticated user.
     * @return A response indicating success or failure.
     */
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody Map<String, Object> request, @RequestAttribute("user") User user) {
        String paymentMethod = (String) request.get("paymentMethod");
        Double amount = (Double) request.get("amount");

        if (paymentMethod == null || amount == null) {
            return ResponseEntity.badRequest().body("Missing required parameters.");
        }

        if (!"STRIPE".equalsIgnoreCase(paymentMethod)) {
            return ResponseEntity.badRequest().body("Unsupported payment method.");
        }

        try {
            // Fetch the cart total for the user
            double cartTotal = cartService.calculateTotalAmount(user);

            // Validate the payment amount
            if (!amount.equals(cartTotal)) {
                return ResponseEntity.badRequest().body("Payment amount does not match cart total. Please try again.");
            }

            // Process the payment
            String paymentResult = paymentService.processPayment(amount);
            return ResponseEntity.ok(paymentResult);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }
}