package com.example.pizzawebapp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public String createPaymentIntent(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100)) // Convert amount to cents
                    .setCurrency("usd")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            logger.info("Payment intent created successfully with ID: {}", paymentIntent.getId());
            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            logger.error("Stripe error while creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }
}