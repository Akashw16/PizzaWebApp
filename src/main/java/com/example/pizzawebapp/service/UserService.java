package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.EmailVerificationToken;
import com.example.pizzawebapp.entity.PasswordResetToken;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.EmailVerificationTokenRepository;
import com.example.pizzawebapp.repository.PasswordResetTokenRepository;
import com.example.pizzawebapp.repository.UserRepository;
import com.example.pizzawebapp.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    // ===================== USER REGISTRATION (AUTO ROLE) =====================
    public void registerUser(User user) {
        // Assign role based on email domain
        String role = user.getUsername().endsWith("@myApp.com") ? "ROLE_ADMIN" : "ROLE_USER";

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        sendVerificationEmail(user);
        logger.info("✅ {} registered successfully with role {}", user.getUsername(), role);
    }

    // ===================== FIND USER =====================
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ===================== SPRING SECURITY =====================
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(user);
    }

    // ===================== ACCOUNT MANAGEMENT =====================
    public User getAuthenticatedUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void updateProfile(String username, Map<String, String> updates) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updates.containsKey("name")) user.setName(updates.get("name"));
        if (updates.containsKey("address")) user.setAddress(updates.get("address"));
        if (updates.containsKey("phone")) user.setPhone(updates.get("phone"));
        userRepository.save(user);
        logger.info("Profile updated successfully for user: {}", username);
    }

    public Map<String, Object> getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", user.getUsername());
        userDetails.put("name", user.getName());
        userDetails.put("address", user.getAddress());
        userDetails.put("phone", user.getPhone());
        userDetails.put("role", user.getRole());
        return userDetails;
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("Account deleted successfully for user: {}", username);
    }

    // ===================== EMAIL VERIFICATION =====================
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);
        logger.info("Email verified successfully for user: {}", user.getUsername());
    }

    // ===================== PASSWORD RESET =====================
    public void requestPasswordReset(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        logger.info("Password reset email sent to: {}", email);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        logger.info("Password reset successfully for user: {}", user.getUsername());
    }

    // ===================== HELPER =====================
    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        emailVerificationTokenRepository.save(verificationToken);

        logger.info("Verification email sent to: {}", user.getUsername());
        logger.info("✅ Use this link in Postman: http://localhost:8080/api/auth/verify-email?token={}", token);
    }
}
