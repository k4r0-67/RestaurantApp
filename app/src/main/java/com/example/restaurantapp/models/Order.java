package com.example.restaurantapp.models;

import java.util.List;

/**
 * Order model representing a placed order in the restaurant app.
 */
public class Order {
    private int id;
    private int userId;
    private List<CartItem> items;
    private double totalPrice;
    private String orderDate;
    private String status;

    public Order() {}

    public Order(int userId, List<CartItem> items, double totalPrice, String orderDate, String status) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(int id, int userId, List<CartItem> items, double totalPrice, String orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
}
