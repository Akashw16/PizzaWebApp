package com.example.pizzawebapp.repository;

import com.example.pizzawebapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    long countByRole(String role);      // <--- used to detect if any admin exists
}
