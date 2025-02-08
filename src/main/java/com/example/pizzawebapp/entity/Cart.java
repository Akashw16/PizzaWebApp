// Cart.java
package com.example.pizzawebapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data // Lombok annotation to generate getters, setters, toString, etc.
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user; // Relationship with the User entity

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>(); // List of cart items

    // Add a helper method to add items to the cart
    public void addItem(CartItem item) {
        this.items.add(item);
    }

    // Add a helper method to remove items from the cart
    public void removeItem(CartItem item) {
        this.items.remove(item);
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}