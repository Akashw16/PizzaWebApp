package com.example.pizzawebapp.dto;

public class PromoResponseDTO {
    private double originalTotal;
    private double discountedTotal;
    private String message;

    public PromoResponseDTO() {}

    public PromoResponseDTO(double originalTotal, double discountedTotal, String message) {
        this.originalTotal = originalTotal;
        this.discountedTotal = discountedTotal;
        this.message = message;
    }

    public double getOriginalTotal() {
        return originalTotal;
    }

    public void setOriginalTotal(double originalTotal) {
        this.originalTotal = originalTotal;
    }

    public double getDiscountedTotal() {
        return discountedTotal;
    }

    public void setDiscountedTotal(double discountedTotal) {
        this.discountedTotal = discountedTotal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
