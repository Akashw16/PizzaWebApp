package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.dto.LoginRequest;
import com.example.pizzawebapp.dto.LoginResponse;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.security.JwtUtil;
import com.example.pizzawebapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // ✅ Manual constructor injection (no Lombok needed)
    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            return ResponseEntity.ok("User registered successfully. Please verify your email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // ✅ Login with JWT and email verification check
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var foundUser = userService.findByUsername(loginRequest.getUsername()).orElse(null);

        if (foundUser == null || !passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        if (!foundUser.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify your email before logging in.");
        }

        // ✅ Generate token directly
        String token = jwtUtil.generateToken(foundUser.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }


    // ✅ Verify email
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            userService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Request password reset
    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        try {
            userService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset email sent.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
