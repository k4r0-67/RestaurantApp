package com.example.restaurantapp.models;

/**
 * CartItem model representing an item in the user's shopping cart.
 */
public class CartItem {
    private int id;
    private FoodItem foodItem;
    private int quantity;

    public CartItem() {}

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public CartItem(int id, FoodItem foodItem, int quantity) {
        this.id = id;
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public FoodItem getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Returns the total price for this cart item (price * quantity).
     */
    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }
}
