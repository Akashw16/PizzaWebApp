package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.UserRepository;
import com.example.pizzawebapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Register a new admin.
     * First admin can be created without authentication (bootstrap).
     * After that, only admins can create new admins.
     * Role will be auto-assigned based on email domain (@myApp.com → ROLE_ADMIN).
     */
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User admin) {
        long adminCount = userRepository.countByRole("ROLE_ADMIN");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isBootstrap = adminCount == 0;

        if (!isBootstrap) {
            if (auth == null || !auth.isAuthenticated() ||
                    auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only an admin can create another admin.");
            }
        }

        try {
            userService.registerUser(admin); // ✅ use registerUser (auto role assignment)
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin created successfully (via @myApp.com domain).");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create admin: " + e.getMessage());
        }
    }

    /**
     * Promote an existing user to admin (admin-only).
     * Only users with @myApp.com domain can be promoted.
     */
    @PostMapping("/promote/{username}")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only an admin can promote users.");
        }

        return userService.findByUsername(username).map(user -> {
            if (!user.getUsername().endsWith("@myApp.com")) {
                return ResponseEntity.badRequest().body("Only @myApp.com users can be admins.");
            }
            user.setRole("ROLE_ADMIN");
            userRepository.save(user);
            return ResponseEntity.ok("User promoted to admin successfully.");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }
}
