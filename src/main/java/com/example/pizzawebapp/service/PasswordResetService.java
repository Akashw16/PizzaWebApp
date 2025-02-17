package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.PasswordResetToken;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.PasswordResetTokenRepository;
import com.example.pizzawebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Request a password reset email.
     *
     * @param email The email address of the user requesting a password reset.
     * @throws RuntimeException If the user is not found or an error occurs during token generation.
     */
    public void requestPasswordReset(String email) {
        // Find the user by their email (username)
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Delete any existing tokens for the user to avoid duplicates
        passwordResetTokenRepository.deleteByUser(user);

        // Generate a unique token and set its expiration time
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token expires in 1 hour

        // Create and save the password reset token
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);

        // Send the password reset email with the token
        emailService.sendPasswordResetEmail(user.getUsername(), token);
    }

    /**
     * Reset the user's password using the provided token.
     *
     * @param token       The password reset token.
     * @param newPassword The new password to set for the user.
     * @throws RuntimeException If the token is invalid, expired, or an error occurs during password update.
     */
    public void resetPassword(String token, String newPassword) {
        // Find the password reset token in the database
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // Check if the token has expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        // Retrieve the associated user and update their password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token after it has been used
        passwordResetTokenRepository.delete(resetToken);
    }
}