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

    public void saveUser(User user) {
        validateEmailDomain(user.getUsername(), "ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        sendVerificationEmail(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    public void createAdmin(User admin) {
        validateEmailDomain(admin.getUsername(), "ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);
        logger.info("Admin created successfully: {}", admin.getUsername());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private void validateEmailDomain(String username, String role) {
        if ("ROLE_ADMIN".equals(role) && !username.endsWith("@myApp.com")) {
            throw new IllegalArgumentException("Admin registration is only allowed for emails ending with '@myApp.com'.");
        }
    }

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

    public User getAuthenticatedUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

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

    private static Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        emailVerificationTokenRepository.save(verificationToken);

        logger.info("Verification email sent to: {}", user.getUsername());
        logger.info("✅ Use this link in Postman: http://localhost:8080/api/auth/verify-email?token={}", token);
    }

}