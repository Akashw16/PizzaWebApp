package com.example.pizzawebapp.service;

import com.example.pizzawebapp.entity.Cart;
import com.example.pizzawebapp.entity.Order;
import com.example.pizzawebapp.entity.OrderItem;
import com.example.pizzawebapp.entity.User;
import com.example.pizzawebapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Create an order for the authenticated user.
     */
    public void createOrder(User user, double amount) {
        Cart cart = user.getCart(); // Get the cart from the user

        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order with an empty cart.");
        }

        // Create the order
        Order order = new Order();
        order.setCustomerName(user.getUsername());
        order.setTotalAmount(amount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");

        // Convert cart items to order items
        order.setItems(cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setPizza(cartItem.getPizza());
                    orderItem.setItemName(cartItem.getPizza().getName()); // Set the pizza name
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setOrder(order); // Link the order item to the order
                    return orderItem;
                })
                .collect(Collectors.toList()));

        // Save the order
        orderRepository.save(order);

        // Clear the cart after creating the order
        cart.getItems().clear();
    }
}