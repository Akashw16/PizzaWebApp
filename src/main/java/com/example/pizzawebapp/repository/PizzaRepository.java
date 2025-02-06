package com.example.pizzawebapp.repository;

import com.example.pizzawebapp.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}