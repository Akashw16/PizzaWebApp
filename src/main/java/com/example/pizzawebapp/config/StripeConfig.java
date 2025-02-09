package com.example.pizzawebapp.config;

import com.stripe.Stripe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    private static final Logger logger = LoggerFactory.getLogger(StripeConfig.class);

    @Value("${stripe.secret-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        if (stripeApiKey == null || stripeApiKey.isEmpty()) {
            logger.error("Stripe API key is not configured. Please check your application.properties file.");
            throw new IllegalArgumentException("Stripe API key is not configured.");
        }
        Stripe.apiKey = stripeApiKey;
        logger.info("Stripe API initialized successfully.");
    }
}