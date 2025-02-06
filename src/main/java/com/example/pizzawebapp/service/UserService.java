package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.UserRepository;
import com.example.pizzawebapp.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save a new user with a default role of ROLE_USER.
     */
    public void saveUser(User user) {
        validateEmailDomain(user.getUsername(), "ROLE_USER"); // Validate email domain for regular users
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // Default role for regular users
        userRepository.save(user);
    }

    /**
     * Create an admin user with the ROLE_ADMIN role.
     * Only allows registration if the username ends with "@myApp.com".
     */
    public void createAdmin(User admin) {
        logger.info("Creating admin with username: " + admin.getUsername());
        validateEmailDomain(admin.getUsername(), "ROLE_ADMIN"); // Validate email domain for admins
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ROLE_ADMIN"); // Assign admin role
        userRepository.save(admin);
        logger.info("Admin created successfully");
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
        if ("ROLE_ADMIN".equals(role)) {
            if (!username.endsWith("@myApp.com")) {
                throw new IllegalArgumentException("Admin registration is only allowed for emails ending with '@myApp.com'.");
            }
        }
    }

    /**
     * Load user details by username for Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new CustomUserDetails(user); // Wrap the User entity in CustomUserDetails
    }
}