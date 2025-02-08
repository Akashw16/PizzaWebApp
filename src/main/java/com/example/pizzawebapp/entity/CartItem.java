// CartItem.java
package com.example.pizzawebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Lombok annotation to generate getters, setters, toString, etc.
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart; // Relationship with the Cart entity

    @ManyToOne
    private Pizza pizza; // Relationship with the Pizza entity

    private Integer quantity; // Quantity of the pizza in the cart

    // Getters and Setters
    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}