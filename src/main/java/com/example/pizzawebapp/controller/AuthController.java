package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.security.JwtUtil;
import com.example.pizzawebapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        User foundUser = userService.findByUsername(user.getUsername())
                .orElse(null);

        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            String token = jwtUtil.generateToken(foundUser.getUsername());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}