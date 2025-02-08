package com.example.pizzawebapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a verification email with a token.
     */
    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify Your Email");
        message.setText("Please click the link below to verify your email:\n"
                + "http://localhost:8080/api/auth/verify-email?token=" + token);
        mailSender.send(message);
    }

    /**
     * Sends a password reset email with a token.
     */
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Please click the link below to reset your password:\n"
                + "http://localhost:8080/api/auth/reset-password?token=" + token);
        mailSender.send(message);
    }
}