package com.example.restaurantapp.models;

/**
 * FoodItem model representing a food item on the restaurant menu.
 */
public class FoodItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private int imageResId;
    private String category;

    public FoodItem() {}

    public FoodItem(int id, String name, String description, double price, int imageResId, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setCategory(String category) { this.category = category; }
}
