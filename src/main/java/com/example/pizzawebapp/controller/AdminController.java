package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody User admin) {
        try {
            userService.createAdmin(admin);
            return ResponseEntity.ok("Admin registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}