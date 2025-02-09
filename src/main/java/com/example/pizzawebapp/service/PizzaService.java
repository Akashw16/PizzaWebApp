package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.Pizza;
import com.example.pizzawebapp.repository.PizzaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {

    @Autowired
    private PizzaRepository pizzaRepository;

    // Get all pizzas from the database
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    // Get pizza by ID from the database
    public Optional<Pizza> getPizzaById(Long id) {
        return pizzaRepository.findById(id);
    }

    // Add a new pizza to the database
    public Pizza addPizza(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }
}