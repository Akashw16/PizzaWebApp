package com.example.pizzawebapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data // Generates getters and setters automatically (if Lombok works)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // Add item to the cart
    public void addItem(CartItem item) {
        if (item == null || item.getPizza() == null) {
            throw new IllegalArgumentException("CartItem or Pizza cannot be null.");
        }
        item.setCart(this);
        this.items.add(item);
    }

    // Remove item from the cart
    public void removeItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("CartItem cannot be null.");
        }
        this.items.remove(item);
        item.setCart(null);
    }

    // Calculate the total price of the cart
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.getPizza().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Manual getters and setters (if Lombok is not working)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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