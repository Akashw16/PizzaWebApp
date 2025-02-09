package com.example.pizzawebapp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${stripe.secret-key}")
    private String stripeApiKey;

    public String processPayment(double amount) throws StripeException {
        // Initialize Stripe with the secret key
        Stripe.apiKey = stripeApiKey;

        // Create a PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // Amount in cents
                .setCurrency("usd")
                .setDescription("Pizza Order Payment")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Return the client secret for the frontend to complete the payment
        return paymentIntent.getClientSecret();
    }
}