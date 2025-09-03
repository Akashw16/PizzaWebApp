//package com.example.pizzawebapp.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
//                                   UserDetailsService userDetailsService,
//                                   PasswordEncoder passwordEncoder) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        // Skip all /api/auth/* endpoints
//        return request.getServletPath().startsWith("/api/auth/");
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain chain, org.springframework.security.core.Authentication authResult)
//            throws IOException, ServletException {
//        // You don’t need this since AuthController generates the JWT
//        chain.doFilter(request, response);
//    }
//}
