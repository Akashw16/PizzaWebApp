package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.service.CartService;
import com.example.pizzawebapp.service.OrderService;
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
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    /**
     * Processes a payment using the specified method and amount.
     */
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody Map<String, Object> request, @RequestAttribute("user") User user) {
        String paymentMethod = (String) request.get("paymentMethod");
        Double amount = ((Number) request.get("amount")).doubleValue(); // Ensure proper type conversion
        Double discountedAmount = request.get("discountedAmount") != null ? ((Number) request.get("discountedAmount")).doubleValue() : null;

        if (paymentMethod == null || amount == null) {
            return ResponseEntity.badRequest().body("Missing required parameters.");
        }

        if (!"STRIPE".equalsIgnoreCase(paymentMethod)) {
            return ResponseEntity.badRequest().body("Unsupported payment method.");
        }

        try {
            // Calculate the cart total for the user
            double cartTotal = cartService.calculateTotalAmount(user);

            // Determine the final amount to charge (use discounted amount if provided)
            double finalAmount = discountedAmount != null ? discountedAmount : cartTotal;

            // Validate that the payment amount matches the final amount
            if (Double.compare(finalAmount, amount) != 0) { // Use Double.compare for precise comparison
                return ResponseEntity.badRequest().body("Payment amount does not match cart total. Please try again.");
            }

            // Process the payment
            String paymentResult = paymentService.processPayment(finalAmount);

            // Create an order with the final amount
            orderService.createOrder(user, finalAmount);

            // Clear the cart after creating the order
            cartService.clearCart(user);

            return ResponseEntity.ok("Payment successful. Cart cleared. Order created.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }
}