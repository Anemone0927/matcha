package com.example.matcha.model;

/**
 * Data Transfer Object (DTO) for transferring cart item data to the Controller and View layers.
 */
public class CartItemModel {
    private Long id;
    private String productName;
    private int quantity;
    private int price; // Unit price

    public CartItemModel() {
    }

    public CartItemModel(Long id, String productName, int quantity, int price) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Calculates the total price for the item
    public int getTotalPrice() {
        return this.price * this.quantity;
    }
}