package com.example.pizzawebapp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final String stripeApiKey;

    public PaymentService(@Value("${stripe.secret-key}") String stripeApiKey) {
        this.stripeApiKey = stripeApiKey;
        Stripe.apiKey = this.stripeApiKey; // Set Stripe API key once
    }

    public String processPayment(double amount) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // Amount in cents
                .setCurrency("usd")
                .setDescription("Pizza Order Payment")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}
