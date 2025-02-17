package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Update the profile of the currently logged-in user.
     */
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody Map<String, String> updates,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getAuthenticatedUser(userDetails);
            userService.updateProfile(user.getUsername(), updates);
            return ResponseEntity.ok("Profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Fetch the details of the currently logged-in user.
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getAuthenticatedUser(userDetails);
            Map<String, Object> userDetailsMap = userService.getUserDetails(user.getUsername());
            return ResponseEntity.ok(userDetailsMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Delete the account of the currently logged-in user.
     */
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getAuthenticatedUser(userDetails);
            userService.deleteUser(user.getUsername());
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}