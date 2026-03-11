package com.example.restaurantapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.restaurantapp.models.CartItem;
import com.example.restaurantapp.models.FoodItem;
import com.example.restaurantapp.models.Order;
import com.example.restaurantapp.models.User;

/**
 * Unit tests for the Restaurant App model classes.
 */
public class ExampleUnitTest {

    @Test
    public void user_model_gettersSetters() {
        User user = new User("John Doe", "john@example.com", "555-1234", "pass123");
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("555-1234", user.getPhone());
        assertEquals("pass123", user.getPassword());

        user.setAddress("123 Main St");
        assertEquals("123 Main St", user.getAddress());
    }

    @Test
    public void foodItem_model_gettersSetters() {
        FoodItem item = new FoodItem(1, "Burger", "Tasty burger", 9.99, 0, "Main Course");
        assertEquals(1, item.getId());
        assertEquals("Burger", item.getName());
        assertEquals(9.99, item.getPrice(), 0.001);
        assertEquals("Main Course", item.getCategory());
    }

    @Test
    public void cartItem_totalPrice_isCorrect() {
        FoodItem food = new FoodItem(1, "Pizza", "Classic pizza", 12.50, 0, "Main Course");
        CartItem cartItem = new CartItem(food, 3);
        assertEquals(37.50, cartItem.getTotalPrice(), 0.001);
    }

    @Test
    public void order_model_gettersSetters() {
        Order order = new Order(1, null, 45.99, "2024-01-15 12:00:00", "Placed");
        assertEquals(1, order.getUserId());
        assertEquals(45.99, order.getTotalPrice(), 0.001);
        assertEquals("Placed", order.getStatus());
    }
}
