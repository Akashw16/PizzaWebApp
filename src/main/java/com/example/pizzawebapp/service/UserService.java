package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.EmailVerificationToken;
import com.example.pizzawebapp.entity.PasswordResetToken;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.EmailVerificationTokenRepository;
import com.example.pizzawebapp.repository.PasswordResetTokenRepository;
import com.example.pizzawebapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Save a new user with a default role of ROLE_USER.
     */
    public void saveUser(User user) {
        validateEmailDomain(user.getUsername(), "ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // Default role for regular users
        userRepository.save(user);
        sendVerificationEmail(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    /**
     * Create an admin user with the ROLE_ADMIN role.
     * Only allows registration if the username ends with "@myApp.com".
     */
    public void createAdmin(User admin) {
        logger.info("Attempting to create admin with username: {}", admin.getUsername());
        validateEmailDomain(admin.getUsername(), "ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ROLE_ADMIN"); // Assign admin role
        userRepository.save(admin);
        logger.info("Admin created successfully: {}", admin.getUsername());
    }

    /**
     * Find a user by their username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Validates the email domain based on the role.
     * For ROLE_ADMIN, the email must end with "@myApp.com".
     */
    private void validateEmailDomain(String username, String role) {
        if ("ROLE_ADMIN".equals(role) && !username.endsWith("@myApp.com")) {
            throw new IllegalArgumentException("Admin registration is only allowed for emails ending with '@myApp.com'.");
        }
    }

    /**
     * Load user details by username for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRole())
        );
    }

    /**
     * Update the profile of the currently logged-in user.
     */
    public void updateProfile(String username, Map<String, String> updates) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        if (updates.containsKey("address")) {
            user.setAddress(updates.get("address"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone"));
        }
        userRepository.save(user);
        logger.info("Profile updated successfully for user: {}", username);
    }

    /**
     * Fetch the details of the currently logged-in user.
     */
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

    /**
     * Delete the account of the currently logged-in user.
     */
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        logger.info("Account deleted successfully for user: {}", username);
    }

    /**
     * Verify email using a token.
     */
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

    /**
     * Request a password reset email.
     */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);

        // Send password reset email
        logger.info("Password reset email sent to: {}", email);
    }

    /**
     * Reset password using a token.
     */
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

    /**
     * Helper method to convert roles into authorities for Spring Security.
     */
    private static Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    /**
     * Sends a verification email to the user.
     */
    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        emailVerificationTokenRepository.save(verificationToken);

        // Trigger email sending logic here
        logger.info("Verification email sent to: {}", user.getUsername());
    }
}