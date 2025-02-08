package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String username = userDetails.getUsername();
        userService.updateProfile(username, updates);
        return ResponseEntity.ok("Profile updated successfully.");
    }

    /**
     * Fetch the details of the currently logged-in user.
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String username = userDetails.getUsername();
        Map<String, Object> userDetailsMap = userService.getUserDetails(username);
        return ResponseEntity.ok(userDetailsMap);
    }

    /**
     * Delete the account of the currently logged-in user.
     */
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String username = userDetails.getUsername();
        userService.deleteUser(username);
        return ResponseEntity.ok("Account deleted successfully.");
    }
}