package com.example.pizzawebapp.controller;

import com.example.pizzawebapp.entity.Pizza;
import com.example.pizzawebapp.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizza")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    // Get all pizzas
    @GetMapping
    public ResponseEntity<List<Pizza>> getAllPizzas() {
        List<Pizza> pizzas = pizzaService.getAllPizzas();
        return ResponseEntity.ok(pizzas);
    }

    // Get pizza by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPizzaById(@PathVariable Long id) {
        return pizzaService.getPizzaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Add a new pizza
    @PostMapping
    public ResponseEntity<Pizza> addPizza(@RequestBody Pizza pizza) {
        Pizza savedPizza = pizzaService.addPizza(pizza);
        return ResponseEntity.status(201).body(savedPizza);
    }
}