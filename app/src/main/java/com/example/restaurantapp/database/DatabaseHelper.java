package com.example.restaurantapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.restaurantapp.models.CartItem;
import com.example.restaurantapp.models.FoodItem;
import com.example.restaurantapp.models.Order;
import com.example.restaurantapp.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseHelper manages the SQLite database for the Restaurant App.
 * Handles user authentication, food items, cart, and order management.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    // Database info
    private static final String DATABASE_NAME = "RestaurantApp.db";
    private static final int DATABASE_VERSION = 5;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FOOD_ITEMS = "food_items";
    public static final String TABLE_CART_ITEMS = "cart_items";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";

    // Common column
    public static final String COLUMN_ID = "id";

    // Users table columns
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ADDRESS = "address";

    // Food items table columns
    public static final String COLUMN_FOOD_NAME = "name";
    public static final String COLUMN_FOOD_DESCRIPTION = "description";
    public static final String COLUMN_FOOD_PRICE = "price";
    public static final String COLUMN_FOOD_IMAGE_RES_ID = "image_res_id";
    public static final String COLUMN_FOOD_CATEGORY = "category";

    // Cart items table columns
    public static final String COLUMN_CART_USER_ID = "user_id";
    public static final String COLUMN_CART_FOOD_ID = "food_id";
    public static final String COLUMN_CART_QUANTITY = "quantity";

    // Orders table columns
    public static final String COLUMN_ORDER_USER_ID = "user_id";
    public static final String COLUMN_ORDER_TOTAL = "total_price";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_STATUS = "status";

    // Order items table columns
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_FOOD_ID = "food_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_ITEM_PRICE = "price";

    // CREATE TABLE statements
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_NAME + " TEXT NOT NULL, " +
            COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
            COLUMN_USER_PHONE + " TEXT, " +
            COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_USER_ADDRESS + " TEXT" +
            ");";

    private static final String CREATE_TABLE_FOOD_ITEMS =
            "CREATE TABLE " + TABLE_FOOD_ITEMS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FOOD_NAME + " TEXT NOT NULL, " +
            COLUMN_FOOD_DESCRIPTION + " TEXT, " +
            COLUMN_FOOD_PRICE + " REAL NOT NULL, " +
            COLUMN_FOOD_IMAGE_RES_ID + " INTEGER, " +
            COLUMN_FOOD_CATEGORY + " TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_CART_ITEMS =
            "CREATE TABLE " + TABLE_CART_ITEMS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CART_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_CART_FOOD_ID + " INTEGER NOT NULL, " +
            COLUMN_CART_QUANTITY + " INTEGER NOT NULL DEFAULT 1, " +
            "FOREIGN KEY(" + COLUMN_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_CART_FOOD_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COLUMN_ID + ")" +
            ");";

    private static final String CREATE_TABLE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ORDER_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_ORDER_TOTAL + " REAL NOT NULL, " +
            COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
            COLUMN_ORDER_STATUS + " TEXT NOT NULL DEFAULT 'Placed', " +
            "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
            ");";

    private static final String CREATE_TABLE_ORDER_ITEMS =
            "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, " +
            COLUMN_ORDER_ITEM_FOOD_ID + " INTEGER NOT NULL, " +
            COLUMN_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, " +
            COLUMN_ORDER_ITEM_PRICE + " REAL NOT NULL, " +
            "FOREIGN KEY(" + COLUMN_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ID + "), " +
            "FOREIGN KEY(" + COLUMN_ORDER_ITEM_FOOD_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COLUMN_ID + ")" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    /**
     * Returns the drawable resource ID by name, or 0 if not found.
     * Use for unique per-item icons (e.g. getDrawableId("ic_food_fries")).
     */
    private int getDrawableId(String drawableName) {
        int id = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return id != 0 ? id : context.getResources().getIdentifier("ic_food_placeholder", "drawable", context.getPackageName());
    }

    /**
     * Returns the drawable resource ID for a food category, or 0 if not found.
     */
    private int getDrawableIdForCategory(String category) {
        String drawableName;
        switch (category) {
            case "Appetizers": drawableName = "ic_food_appetizer"; break;
            case "Main Course": drawableName = "ic_food_main"; break;
            case "Desserts": drawableName = "ic_food_dessert"; break;
            case "Beverages": drawableName = "ic_food_beverage"; break;
            case "Sides": drawableName = "ic_food_sides"; break;
            case "Breakfast": drawableName = "ic_food_breakfast"; break;
            case "Seafood": drawableName = "ic_food_seafood"; break;
            case "Salads": drawableName = "ic_food_salads"; break;
            default: return 0;
        }
        return context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FOOD_ITEMS);
        db.execSQL(CREATE_TABLE_CART_ITEMS);
        db.execSQL(CREATE_TABLE_ORDERS);
        db.execSQL(CREATE_TABLE_ORDER_ITEMS);
        populateSampleFoodItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Refresh menu items when upgrading so new food items (and unique icons) appear without losing user data
        if (oldVersion < 3) {
            db.delete(TABLE_CART_ITEMS, null, null);  // Clear cart so it doesn't reference old food IDs
            db.delete(TABLE_FOOD_ITEMS, null, null);
            populateSampleFoodItems(db);
        }
        // For older upgrade path (full wipe), uncomment below and remove the if block above:
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // onCreate(db);
    }

    // ===================== USER OPERATIONS =====================

    /**
     * Register a new user. Returns the row ID, or -1 if the email already exists.
     */
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    /**
     * Check if a user exists with the given email and password.
     */
    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    /**
     * Check if an email is already registered.
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    /**
     * Get a user by their ID.
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            db.close();
            return user;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    /**
     * Update a user's profile information.
     */
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PHONE, user.getPhone());
        values.put(COLUMN_USER_ADDRESS, user.getAddress());
        int rowsAffected = db.update(TABLE_USERS, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return rowsAffected;
    }

    private User cursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS))
        );
    }

    // ===================== FOOD ITEM OPERATIONS =====================

    /**
     * Get all food items.
     */
    public List<FoodItem> getAllFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                foodItems.add(cursorToFoodItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return foodItems;
    }

    /**
     * Get food items filtered by category.
     */
    public List<FoodItem> getFoodItemsByCategory(String category) {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, null,
                COLUMN_FOOD_CATEGORY + "=?",
                new String[]{category},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                foodItems.add(cursorToFoodItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return foodItems;
    }

    /**
     * Get a food item by ID.
     */
    public FoodItem getFoodItemById(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(foodId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            FoodItem item = cursorToFoodItem(cursor);
            cursor.close();
            db.close();
            return item;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    /**
     * Search food items by name or description.
     */
    public List<FoodItem> searchFoodItems(String query) {
        List<FoodItem> foodItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "%" + query + "%";
        Cursor cursor = db.query(TABLE_FOOD_ITEMS, null,
                COLUMN_FOOD_NAME + " LIKE ? OR " + COLUMN_FOOD_DESCRIPTION + " LIKE ?",
                new String[]{searchQuery, searchQuery},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                foodItems.add(cursorToFoodItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return foodItems;
    }

    private FoodItem cursorToFoodItem(Cursor cursor) {
        return new FoodItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE_RES_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CATEGORY))
        );
    }

    // ===================== CART OPERATIONS =====================

    /**
     * Add a food item to the cart. If already in cart, increases quantity.
     */
    public void addToCart(int userId, int foodId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the item is already in the cart
        Cursor cursor = db.query(TABLE_CART_ITEMS, null,
                COLUMN_CART_USER_ID + "=? AND " + COLUMN_CART_FOOD_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(foodId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Item exists - update quantity
            int currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY));
            int cartId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_QUANTITY, currentQty + quantity);
            db.update(TABLE_CART_ITEMS, values, COLUMN_ID + "=?", new String[]{String.valueOf(cartId)});
        } else {
            // New item - insert
            if (cursor != null) cursor.close();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CART_USER_ID, userId);
            values.put(COLUMN_CART_FOOD_ID, foodId);
            values.put(COLUMN_CART_QUANTITY, quantity);
            db.insert(TABLE_CART_ITEMS, null, values);
        }
        db.close();
    }

    /**
     * Update the quantity of a cart item.
     */
    public void updateCartItemQuantity(int cartItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_QUANTITY, quantity);
        db.update(TABLE_CART_ITEMS, values, COLUMN_ID + "=?", new String[]{String.valueOf(cartItemId)});
        db.close();
    }

    /**
     * Remove a specific item from the cart.
     */
    public void removeCartItem(int cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART_ITEMS, COLUMN_ID + "=?", new String[]{String.valueOf(cartItemId)});
        db.close();
    }

    /**
     * Clear all cart items for a user.
     */
    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART_ITEMS, COLUMN_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    /**
     * Get all cart items for a user.
     */
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT ci." + COLUMN_ID + ", ci." + COLUMN_CART_QUANTITY +
                ", fi." + COLUMN_ID + " AS food_id, fi." + COLUMN_FOOD_NAME +
                ", fi." + COLUMN_FOOD_DESCRIPTION + ", fi." + COLUMN_FOOD_PRICE +
                ", fi." + COLUMN_FOOD_IMAGE_RES_ID + ", fi." + COLUMN_FOOD_CATEGORY +
                " FROM " + TABLE_CART_ITEMS + " ci" +
                " JOIN " + TABLE_FOOD_ITEMS + " fi ON ci." + COLUMN_CART_FOOD_ID + " = fi." + COLUMN_ID +
                " WHERE ci." + COLUMN_CART_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                FoodItem foodItem = new FoodItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("food_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE_RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CATEGORY))
                );
                CartItem cartItem = new CartItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        foodItem,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CART_QUANTITY))
                );
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return cartItems;
    }

    /**
     * Get the total number of items in the cart for a user.
     */
    public int getCartItemCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_CART_QUANTITY + ") FROM " + TABLE_CART_ITEMS +
                " WHERE " + COLUMN_CART_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    // ===================== ORDER OPERATIONS =====================

    /**
     * Place an order from the current cart. Clears cart after placing.
     */
    public long placeOrder(int userId, List<CartItem> cartItems, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;

        try {
            // Insert order
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_USER_ID, userId);
            orderValues.put(COLUMN_ORDER_TOTAL, totalPrice);
            orderValues.put(COLUMN_ORDER_DATE, date);
            orderValues.put(COLUMN_ORDER_STATUS, "Placed");
            orderId = db.insert(TABLE_ORDERS, null, orderValues);

            // Insert order items
            for (CartItem item : cartItems) {
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COLUMN_ORDER_ITEM_FOOD_ID, item.getFoodItem().getId());
                itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, item.getQuantity());
                itemValues.put(COLUMN_ORDER_ITEM_PRICE, item.getFoodItem().getPrice());
                db.insert(TABLE_ORDER_ITEMS, null, itemValues);
            }

            // Clear cart
            db.delete(TABLE_CART_ITEMS, COLUMN_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return orderId;
    }

    /**
     * Get order history for a user.
     */
    public List<Order> getOrderHistory(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor orderCursor = db.query(TABLE_ORDERS, null,
                COLUMN_ORDER_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, COLUMN_ID + " DESC");

        if (orderCursor != null && orderCursor.moveToFirst()) {
            do {
                int orderId = orderCursor.getInt(orderCursor.getColumnIndexOrThrow(COLUMN_ID));
                double total = orderCursor.getDouble(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL));
                String date = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                String status = orderCursor.getString(orderCursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));

                List<CartItem> items = getOrderItems(db, orderId);
                Order order = new Order(orderId, userId, items, total, date, status);
                orders.add(order);
            } while (orderCursor.moveToNext());
            orderCursor.close();
        }
        db.close();
        return orders;
    }

    /**
     * Get items for a specific order.
     */
    private List<CartItem> getOrderItems(SQLiteDatabase db, int orderId) {
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT oi." + COLUMN_ORDER_ITEM_QUANTITY +
                ", fi." + COLUMN_ID + " AS food_id, fi." + COLUMN_FOOD_NAME +
                ", fi." + COLUMN_FOOD_DESCRIPTION + ", fi." + COLUMN_FOOD_PRICE +
                ", fi." + COLUMN_FOOD_IMAGE_RES_ID + ", fi." + COLUMN_FOOD_CATEGORY +
                " FROM " + TABLE_ORDER_ITEMS + " oi" +
                " JOIN " + TABLE_FOOD_ITEMS + " fi ON oi." + COLUMN_ORDER_ITEM_FOOD_ID + " = fi." + COLUMN_ID +
                " WHERE oi." + COLUMN_ORDER_ITEM_ORDER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                FoodItem foodItem = new FoodItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("food_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FOOD_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE_RES_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_CATEGORY))
                );
                items.add(new CartItem(foodItem, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ITEM_QUANTITY))));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    // ===================== SAMPLE DATA =====================

    /**
     * Pre-populate the food_items table with sample menu data.
     */
    private void populateSampleFoodItems(SQLiteDatabase db) {
        // Appetizers - each with unique icon
        insertFoodItem(db, "Spring Rolls", "Crispy fried rolls filled with vegetables and served with sweet chili sauce.", 6.99, getDrawableId("ic_food_roll"), "Appetizers");
        insertFoodItem(db, "Chicken Soup", "Hearty homemade chicken soup with vegetables and herbs.", 5.49, getDrawableId("ic_food_soup"), "Appetizers");
        insertFoodItem(db, "Bruschetta", "Toasted bread topped with fresh tomatoes, garlic, basil, and olive oil.", 7.99, getDrawableId("ic_food_bread"), "Appetizers");
        insertFoodItem(db, "Mozzarella Sticks", "Golden-fried mozzarella sticks served with marinara sauce.", 8.49, getDrawableId("ic_food_cheese"), "Appetizers");
        insertFoodItem(db, "Chicken Wings", "Crispy chicken wings tossed in your choice of buffalo or BBQ sauce.", 11.99, getDrawableId("ic_food_wings"), "Appetizers");
        insertFoodItem(db, "Hummus & Pita", "Creamy chickpea hummus with warm pita bread and olive oil.", 6.49, getDrawableId("ic_food_dip"), "Appetizers");
        insertFoodItem(db, "Shrimp Cocktail", "Chilled shrimp served with tangy cocktail sauce and lemon.", 12.49, getDrawableId("ic_food_shrimp"), "Appetizers");
        insertFoodItem(db, "Stuffed Mushrooms", "Baked mushrooms stuffed with cream cheese, garlic, and herbs.", 9.99, getDrawableId("ic_food_mushroom"), "Appetizers");
        insertFoodItem(db, "Nachos", "Tortilla chips topped with cheese, jalapeños, sour cream, and guacamole.", 10.49, getDrawableId("ic_food_nachos"), "Appetizers");
        insertFoodItem(db, "Garlic Bread", "Toasted baguette with butter and roasted garlic, topped with parsley.", 5.99, getDrawableId("ic_food_bread"), "Appetizers");
        insertFoodItem(db, "Onion Rings", "Crispy beer-battered onion rings with ranch or ketchup.", 7.49, getDrawableId("ic_food_onion_rings"), "Appetizers");

        // Main Course
        insertFoodItem(db, "Grilled Chicken", "Juicy grilled chicken breast with herbs, served with seasonal vegetables.", 14.99, getDrawableId("ic_food_chicken"), "Main Course");
        insertFoodItem(db, "Spaghetti Carbonara", "Classic Italian pasta with creamy egg sauce, pancetta, and Parmesan.", 13.99, getDrawableId("ic_food_pasta"), "Main Course");
        insertFoodItem(db, "Ribeye Steak", "Premium 12oz ribeye steak cooked to your preference with garlic butter.", 28.99, getDrawableId("ic_food_steak"), "Main Course");
        insertFoodItem(db, "Classic Burger", "Beef patty with lettuce, tomato, cheese, and special sauce on a brioche bun.", 12.99, getDrawableId("ic_food_burger"), "Main Course");
        insertFoodItem(db, "Margherita Pizza", "Classic pizza with fresh mozzarella, tomato sauce, and basil.", 14.49, getDrawableId("ic_food_pizza"), "Main Course");
        insertFoodItem(db, "Salmon Fillet", "Pan-seared salmon with lemon butter sauce and asparagus.", 19.99, getDrawableId("ic_food_salmon"), "Main Course");
        insertFoodItem(db, "Veggie Stir Fry", "Fresh seasonal vegetables stir-fried in savory sauce, served over rice.", 11.99, getDrawableId("ic_food_vegetables"), "Main Course");
        insertFoodItem(db, "Chicken Parmesan", "Breaded chicken breast with marinara and melted mozzarella over spaghetti.", 16.99, getDrawableId("ic_food_chicken"), "Main Course");
        insertFoodItem(db, "Fish and Chips", "Beer-battered cod with crispy fries, coleslaw, and tartar sauce.", 15.49, getDrawableId("ic_food_fish"), "Main Course");
        insertFoodItem(db, "Beef Tacos", "Three soft tacos with seasoned beef, lettuce, cheese, and salsa.", 11.49, getDrawableId("ic_food_tacos"), "Main Course");
        insertFoodItem(db, "Lamb Chops", "Grilled lamb chops with mint sauce and roasted potatoes.", 24.99, getDrawableId("ic_food_steak"), "Main Course");
        insertFoodItem(db, "Vegetable Curry", "Creamy coconut curry with mixed vegetables and basmati rice.", 13.49, getDrawableId("ic_food_curry"), "Main Course");
        insertFoodItem(db, "BBQ Ribs", "Slow-cooked pork ribs with house BBQ sauce and coleslaw.", 22.99, getDrawableId("ic_food_ribs"), "Main Course");
        insertFoodItem(db, "Pad Thai", "Stir-fried rice noodles with shrimp, peanuts, and tamarind sauce.", 14.99, getDrawableId("ic_food_noodles"), "Main Course");
        insertFoodItem(db, "Lasagna", "Layers of pasta, beef ragù, béchamel, and melted cheese.", 15.99, getDrawableId("ic_food_pasta"), "Main Course");

        // Desserts
        insertFoodItem(db, "Chocolate Lava Cake", "Warm chocolate cake with a gooey molten center, served with vanilla ice cream.", 7.99, getDrawableId("ic_food_cake"), "Desserts");
        insertFoodItem(db, "Vanilla Ice Cream", "Three scoops of premium vanilla ice cream with your choice of toppings.", 5.49, getDrawableId("ic_food_ice_cream"), "Desserts");
        insertFoodItem(db, "Tiramisu", "Classic Italian dessert with espresso-soaked ladyfingers and mascarpone cream.", 8.49, getDrawableId("ic_food_tiramisu"), "Desserts");
        insertFoodItem(db, "Cheesecake", "New York-style cheesecake with strawberry topping on a graham cracker crust.", 7.49, getDrawableId("ic_food_cheesecake"), "Desserts");
        insertFoodItem(db, "Crème Brûlée", "Classic French vanilla custard with a caramelized sugar topping.", 8.99, getDrawableId("ic_food_cake"), "Desserts");
        insertFoodItem(db, "Brownie Sundae", "Warm chocolate brownie with ice cream, whipped cream, and chocolate sauce.", 7.49, getDrawableId("ic_food_ice_cream"), "Desserts");
        insertFoodItem(db, "Apple Pie", "Classic apple pie with cinnamon, served warm with vanilla ice cream.", 6.99, getDrawableId("ic_food_pie"), "Desserts");
        insertFoodItem(db, "Panna Cotta", "Silky vanilla panna cotta with berry compote.", 7.99, getDrawableId("ic_food_cake"), "Desserts");
        insertFoodItem(db, "Churros", "Crispy fried dough with cinnamon sugar and chocolate dipping sauce.", 6.49, getDrawableId("ic_food_pie"), "Desserts");
        insertFoodItem(db, "Key Lime Pie", "Tangy key lime pie with graham cracker crust and whipped cream.", 7.99, getDrawableId("ic_food_pie"), "Desserts");
        insertFoodItem(db, "Mousse au Chocolat", "Light and rich dark chocolate mousse with whipped cream.", 8.49, getDrawableId("ic_food_cake"), "Desserts");
        insertFoodItem(db, "Baklava", "Layers of phyllo pastry with honey and walnuts.", 7.99, getDrawableId("ic_food_pie"), "Desserts");

        // Beverages
        insertFoodItem(db, "Espresso", "Rich and bold single or double shot of espresso.", 2.99, getDrawableId("ic_food_coffee"), "Beverages");
        insertFoodItem(db, "Fresh Orange Juice", "Freshly squeezed orange juice, served chilled.", 4.49, getDrawableId("ic_food_juice"), "Beverages");
        insertFoodItem(db, "Mango Smoothie", "Creamy blended mango smoothie with a hint of lime.", 5.99, getDrawableId("ic_food_smoothie"), "Beverages");
        insertFoodItem(db, "Lemonade", "Freshly made lemonade with mint, served over ice.", 3.99, getDrawableId("ic_food_lemonade"), "Beverages");
        insertFoodItem(db, "Cappuccino", "Espresso with steamed milk foam, optionally dusted with cocoa.", 4.49, getDrawableId("ic_food_coffee"), "Beverages");
        insertFoodItem(db, "Iced Coffee", "Chilled coffee over ice with your choice of milk and sweetener.", 4.99, getDrawableId("ic_food_coffee"), "Beverages");
        insertFoodItem(db, "Green Tea", "Premium Japanese green tea, hot or iced.", 3.49, getDrawableId("ic_food_tea"), "Beverages");
        insertFoodItem(db, "Milkshake", "Thick vanilla, chocolate, or strawberry milkshake.", 5.49, getDrawableId("ic_food_smoothie"), "Beverages");
        insertFoodItem(db, "Sparkling Water", "Refreshing sparkling water with a slice of lemon or lime.", 2.49, getDrawableId("ic_food_juice"), "Beverages");
        insertFoodItem(db, "Hot Chocolate", "Rich hot chocolate topped with whipped cream and marshmallows.", 4.99, getDrawableId("ic_food_coffee"), "Beverages");
        insertFoodItem(db, "Iced Tea", "Freshly brewed iced tea with lemon, sweetened or unsweetened.", 3.49, getDrawableId("ic_food_tea"), "Beverages");
        insertFoodItem(db, "Smoothie Bowl", "Blended acai or berry smoothie topped with granola and fruit.", 6.99, getDrawableId("ic_food_smoothie"), "Beverages");

        // Sides - each with unique icon
        insertFoodItem(db, "French Fries", "Crispy golden fries with sea salt, served with ketchup or mayo.", 4.99, getDrawableId("ic_food_fries"), "Sides");
        insertFoodItem(db, "Mashed Potatoes", "Creamy mashed potatoes with butter and herbs.", 4.49, getDrawableId("ic_food_potato"), "Sides");
        insertFoodItem(db, "Garlic Mashed Potatoes", "Mashed potatoes with roasted garlic and parsley.", 5.49, getDrawableId("ic_food_potato"), "Sides");
        insertFoodItem(db, "Coleslaw", "Creamy cabbage and carrot coleslaw with a tangy dressing.", 3.99, getDrawableId("ic_food_coleslaw"), "Sides");
        insertFoodItem(db, "Mac and Cheese", "Creamy macaroni with a blend of cheddar and mozzarella.", 5.99, getDrawableId("ic_food_mac_cheese"), "Sides");
        insertFoodItem(db, "Steamed Vegetables", "Seasonal vegetables steamed with a light butter glaze.", 4.99, getDrawableId("ic_food_vegetables"), "Sides");
        insertFoodItem(db, "Rice Pilaf", "Fragrant rice cooked with herbs and toasted orzo.", 4.49, getDrawableId("ic_food_rice"), "Sides");
        insertFoodItem(db, "Sweet Potato Fries", "Crispy sweet potato fries with chipotle mayo.", 5.49, getDrawableId("ic_food_fries"), "Sides");
        insertFoodItem(db, "Onion Rings", "Beer-battered onion rings with ranch dipping sauce.", 5.99, getDrawableId("ic_food_onion_rings"), "Sides");
        insertFoodItem(db, "Corn on the Cob", "Grilled corn with butter and optional chili lime seasoning.", 3.49, getDrawableId("ic_food_corn"), "Sides");
        insertFoodItem(db, "Loaded Baked Potato", "Baked potato with cheddar, bacon, sour cream, and chives.", 6.49, getDrawableId("ic_food_potato"), "Sides");
        insertFoodItem(db, "Truffle Fries", "Crispy fries tossed with truffle oil and parmesan.", 7.99, getDrawableId("ic_food_fries"), "Sides");
        insertFoodItem(db, "Grilled Asparagus", "Fresh asparagus with lemon butter and parmesan.", 5.99, getDrawableId("ic_food_vegetables"), "Sides");
        insertFoodItem(db, "Roasted Brussels Sprouts", "Crispy Brussels sprouts with balsamic glaze.", 5.49, getDrawableId("ic_food_vegetables"), "Sides");
        insertFoodItem(db, "Garlic Breadsticks", "Warm breadsticks with garlic butter and marinara.", 4.99, getDrawableId("ic_food_bread"), "Sides");
        insertFoodItem(db, "Jalapeño Poppers", "Cream cheese–stuffed jalapeños, breaded and fried.", 6.99, getDrawableId("ic_food_cheese"), "Sides");
        insertFoodItem(db, "House Salad Side", "Mixed greens with tomato and choice of dressing.", 3.99, getDrawableId("ic_food_salad_bowl"), "Sides");
        insertFoodItem(db, "Black Beans & Rice", "Seasoned black beans over cilantro lime rice.", 4.49, getDrawableId("ic_food_beans"), "Sides");
        insertFoodItem(db, "Baked Beans", "Slow-cooked navy beans in sweet and smoky sauce.", 4.99, getDrawableId("ic_food_beans"), "Sides");
        insertFoodItem(db, "Creamed Spinach", "Creamy spinach with garlic and nutmeg.", 5.49, getDrawableId("ic_food_vegetables"), "Sides");
        insertFoodItem(db, "Crispy Tater Tots", "Golden tater tots with ketchup or cheese sauce.", 4.49, getDrawableId("ic_food_fries"), "Sides");
        insertFoodItem(db, "Green Beans Almondine", "Blanched green beans with toasted almonds and butter.", 5.99, getDrawableId("ic_food_vegetables"), "Sides");
        insertFoodItem(db, "Hash Browns", "Crispy shredded potato hash browns.", 4.99, getDrawableId("ic_food_potato"), "Sides");
        insertFoodItem(db, "Polenta", "Creamy Italian polenta with parmesan and herbs.", 5.49, getDrawableId("ic_food_rice"), "Sides");
        insertFoodItem(db, "Side of Fruit", "Fresh seasonal fruit cup.", 4.49, getDrawableId("ic_food_juice"), "Sides");
        insertFoodItem(db, "Twice-Baked Potato", "Baked potato stuffed with cheese, bacon, and sour cream.", 6.99, getDrawableId("ic_food_potato"), "Sides");

        // Breakfast - each with unique icon
        insertFoodItem(db, "Pancakes", "Stack of fluffy buttermilk pancakes with maple syrup and butter.", 8.99, getDrawableId("ic_food_pancake"), "Breakfast");
        insertFoodItem(db, "Eggs Benedict", "Poached eggs on English muffin with Canadian bacon and hollandaise.", 12.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Avocado Toast", "Sourdough toast with smashed avocado, cherry tomatoes, and feta.", 9.49, getDrawableId("ic_food_bread"), "Breakfast");
        insertFoodItem(db, "Full English Breakfast", "Eggs, bacon, sausage, beans, tomatoes, mushrooms, and toast.", 14.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Omelette", "Three-egg omelette with your choice of cheese, veggies, and meat.", 10.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Waffles", "Belgian waffles with whipped cream and fresh berries.", 9.99, getDrawableId("ic_food_waffle"), "Breakfast");
        insertFoodItem(db, "French Toast", "Brioche French toast with cinnamon and powdered sugar.", 8.49, getDrawableId("ic_food_pancake"), "Breakfast");
        insertFoodItem(db, "Breakfast Burrito", "Scrambled eggs, cheese, potatoes, and salsa in a flour tortilla.", 10.49, getDrawableId("ic_food_tacos"), "Breakfast");
        insertFoodItem(db, "Granola & Yogurt", "House granola with Greek yogurt and seasonal fruit.", 7.99, getDrawableId("ic_food_oatmeal"), "Breakfast");
        insertFoodItem(db, "Croissant & Jam", "Fresh butter croissant with strawberry or apricot jam.", 5.49, getDrawableId("ic_food_croissant"), "Breakfast");
        insertFoodItem(db, "Scrambled Eggs & Bacon", "Fluffy scrambled eggs with crispy bacon and toast.", 9.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Breakfast Bowl", "Quinoa, black beans, avocado, egg, and salsa.", 11.49, getDrawableId("ic_food_rice"), "Breakfast");
        insertFoodItem(db, "Smoked Salmon Bagel", "Toasted bagel with cream cheese, capers, and smoked salmon.", 12.99, getDrawableId("ic_food_bagel"), "Breakfast");
        insertFoodItem(db, "Huevos Rancheros", "Fried eggs on tortillas with ranchero sauce, beans, and cheese.", 10.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Steel-Cut Oatmeal", "Oatmeal with brown sugar, berries, and nuts.", 7.49, getDrawableId("ic_food_oatmeal"), "Breakfast");
        insertFoodItem(db, "Breakfast Quesadilla", "Flour tortilla with eggs, cheese, and chorizo.", 9.49, getDrawableId("ic_food_tacos"), "Breakfast");
        insertFoodItem(db, "Belgian Waffle Combo", "Waffle with two eggs and your choice of bacon or sausage.", 13.99, getDrawableId("ic_food_waffle"), "Breakfast");
        insertFoodItem(db, "Fruit Parfait", "Layers of yogurt, granola, and fresh seasonal fruit.", 8.49, getDrawableId("ic_food_smoothie"), "Breakfast");
        insertFoodItem(db, "Breakfast Sandwich", "Egg, cheese, and choice of meat on English muffin or croissant.", 8.99, getDrawableId("ic_food_sandwich"), "Breakfast");
        insertFoodItem(db, "Cinnamon Roll", "Warm cinnamon roll with cream cheese frosting.", 6.99, getDrawableId("ic_food_croissant"), "Breakfast");
        insertFoodItem(db, "Eggs Florentine", "Poached eggs on spinach and English muffin with hollandaise.", 11.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Breakfast Skillet", "Potatoes, eggs, cheese, and choice of meat in a cast-iron skillet.", 12.49, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Banana Bread", "Fresh-baked banana bread with walnuts, served warm.", 5.99, getDrawableId("ic_food_bread"), "Breakfast");
        insertFoodItem(db, "Protein Bowl", "Eggs, quinoa, black beans, avocado, and salsa.", 11.99, getDrawableId("ic_food_rice"), "Breakfast");
        insertFoodItem(db, "Breakfast Tacos", "Three soft tacos with eggs, cheese, and choice of bacon or chorizo.", 9.99, getDrawableId("ic_food_tacos"), "Breakfast");
        insertFoodItem(db, "Overnight Oats", "Oats soaked in milk with berries, nuts, and honey.", 6.49, getDrawableId("ic_food_oatmeal"), "Breakfast");
        insertFoodItem(db, "Eggs in a Basket", "Egg fried in the center of buttered toast.", 7.49, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Breakfast Potatoes", "Seasoned roasted breakfast potatoes with onions and peppers.", 4.99, getDrawableId("ic_food_potato"), "Breakfast");
        insertFoodItem(db, "Blueberry Pancakes", "Stack of pancakes loaded with fresh blueberries and maple syrup.", 9.49, getDrawableId("ic_food_pancake"), "Breakfast");
        insertFoodItem(db, "Chocolate Chip Waffles", "Belgian waffles with chocolate chips and whipped cream.", 10.49, getDrawableId("ic_food_waffle"), "Breakfast");
        insertFoodItem(db, "Shakshuka", "Eggs poached in spiced tomato and pepper sauce with bread.", 11.99, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Breakfast Hash", "Crispy potatoes, onions, peppers, and choice of meat with eggs.", 10.99, getDrawableId("ic_food_potato"), "Breakfast");
        insertFoodItem(db, "Muesli", "Swiss-style muesli with oats, nuts, dried fruit, and milk or yogurt.", 7.99, getDrawableId("ic_food_oatmeal"), "Breakfast");
        insertFoodItem(db, "Egg White Omelette", "Fluffy egg white omelette with vegetables and feta.", 10.49, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Biscuits and Gravy", "Flaky biscuits smothered in creamy sausage gravy.", 8.99, getDrawableId("ic_food_bread"), "Breakfast");
        insertFoodItem(db, "Breakfast Wrap", "Scrambled eggs, cheese, and veggies in a flour tortilla.", 8.49, getDrawableId("ic_food_sandwich"), "Breakfast");
        insertFoodItem(db, "French Toast Sticks", "Crispy French toast sticks with maple syrup for dipping.", 7.99, getDrawableId("ic_food_pancake"), "Breakfast");
        insertFoodItem(db, "Acai Bowl", "Blended acai topped with granola, banana, and honey.", 9.99, getDrawableId("ic_food_smoothie"), "Breakfast");
        insertFoodItem(db, "Corned Beef Hash", "Crispy corned beef hash with onions and two eggs any style.", 12.49, getDrawableId("ic_food_egg"), "Breakfast");
        insertFoodItem(db, "Chilaquiles", "Tortilla chips in salsa with eggs, cheese, and crema.", 10.99, getDrawableId("ic_food_tacos"), "Breakfast");
        insertFoodItem(db, "Breakfast Parfait", "Layered Greek yogurt, granola, and fresh berries.", 7.49, getDrawableId("ic_food_smoothie"), "Breakfast");
        insertFoodItem(db, "Sausage & Egg Platter", "Two eggs, sausage links, hash browns, and toast.", 11.99, getDrawableId("ic_food_egg"), "Breakfast");

        // Seafood - each with unique icon
        insertFoodItem(db, "Grilled Shrimp", "Jumbo shrimp grilled with garlic butter and lemon.", 18.99, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Fish Tacos", "Beer-battered white fish in soft tortillas with slaw and lime crema.", 13.99, getDrawableId("ic_food_tacos"), "Seafood");
        insertFoodItem(db, "Lobster Tail", "Broiled lobster tail with drawn butter and lemon.", 34.99, getDrawableId("ic_food_lobster"), "Seafood");
        insertFoodItem(db, "Crab Cakes", "Two golden crab cakes with remoulade and lemon.", 16.99, getDrawableId("ic_food_crab"), "Seafood");
        insertFoodItem(db, "Calamari", "Crispy fried calamari with marinara and lemon aioli.", 12.49, getDrawableId("ic_food_calamari"), "Seafood");
        insertFoodItem(db, "Seafood Paella", "Saffron rice with shrimp, mussels, clams, and chorizo.", 24.99, getDrawableId("ic_food_rice"), "Seafood");
        insertFoodItem(db, "Grilled Salmon", "Atlantic salmon with herb butter and seasonal vegetables.", 22.99, getDrawableId("ic_food_salmon"), "Seafood");
        insertFoodItem(db, "Shrimp Scampi", "Shrimp in garlic white wine sauce over linguine.", 19.99, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Clam Chowder", "New England style creamy clam chowder with bacon.", 8.99, getDrawableId("ic_food_soup"), "Seafood");
        insertFoodItem(db, "Tuna Steak", "Seared tuna steak with ginger soy glaze and edamame.", 23.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Mussels in White Wine", "Steamed mussels in garlic white wine broth with bread.", 16.49, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Fish and Chips", "Beer-battered cod with fries, coleslaw, and tartar sauce.", 15.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Cajun Shrimp", "Spicy Cajun-seasoned shrimp with rice and vegetables.", 19.49, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Lobster Roll", "Chilled lobster meat in buttered roll with mayo and celery.", 26.99, getDrawableId("ic_food_lobster"), "Seafood");
        insertFoodItem(db, "Sushi Platter", "Assorted nigiri and maki with soy sauce and wasabi.", 28.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Coconut Shrimp", "Crispy coconut-battered shrimp with sweet chili sauce.", 17.99, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Grilled Swordfish", "Swordfish steak with lemon herb butter and vegetables.", 24.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Seafood Linguine", "Linguine with shrimp, scallops, and clams in garlic tomato sauce.", 22.49, getDrawableId("ic_food_pasta"), "Seafood");
        insertFoodItem(db, "Oysters Rockefeller", "Baked oysters with spinach, cheese, and breadcrumbs.", 18.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Grilled Mahi-Mahi", "Mahi-mahi with mango salsa and coconut rice.", 21.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Ceviche", "Fresh raw fish cured in citrus with onion, cilantro, and avocado.", 14.99, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Shrimp & Grits", "Sautéed shrimp over creamy stone-ground grits.", 17.99, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Baked Cod", "Atlantic cod with lemon, herbs, and breadcrumb topping.", 18.49, getDrawableId("ic_food_fish"), "Seafood");
        insertFoodItem(db, "Seafood Bisque", "Rich creamy soup with lobster, shrimp, and crab.", 10.99, getDrawableId("ic_food_soup"), "Seafood");
        insertFoodItem(db, "Scallops", "Pan-seared sea scallops with brown butter and capers.", 26.99, getDrawableId("ic_food_shrimp"), "Seafood");
        insertFoodItem(db, "Crab Legs", "Steamed snow crab legs with drawn butter.", 32.99, getDrawableId("ic_food_crab"), "Seafood");
        insertFoodItem(db, "Fish Curry", "White fish in coconut curry with vegetables and rice.", 16.99, getDrawableId("ic_food_curry"), "Seafood");
        insertFoodItem(db, "Garlic Butter Shrimp", "Sautéed shrimp in garlic butter with crusty bread.", 19.49, getDrawableId("ic_food_shrimp"), "Seafood");

        // Salads - each with unique icon
        insertFoodItem(db, "Caesar Salad", "Crisp romaine with parmesan, croutons, and classic Caesar dressing.", 8.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Greek Salad", "Cucumbers, tomatoes, olives, feta, and red onion with oregano dressing.", 9.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Garden Salad", "Mixed greens with tomatoes, cucumber, carrots, and choice of dressing.", 7.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Cobb Salad", "Chicken, bacon, egg, avocado, blue cheese, and tomato on mixed greens.", 12.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Asian Chicken Salad", "Shredded chicken, cabbage, carrots, and sesame ginger dressing.", 11.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Caprese Salad", "Fresh mozzarella, tomatoes, and basil with balsamic glaze.", 10.99, getDrawableId("ic_food_cheese"), "Salads");
        insertFoodItem(db, "Quinoa Salad", "Quinoa with roasted vegetables, feta, and lemon herb dressing.", 10.49, getDrawableId("ic_food_rice"), "Salads");
        insertFoodItem(db, "Waldorf Salad", "Apples, walnuts, celery, and grapes in creamy dressing.", 9.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Nicoise Salad", "Tuna, green beans, egg, olives, and potatoes with vinaigrette.", 13.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Kale Salad", "Massaged kale with cranberries, almonds, and tahini dressing.", 9.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Southwest Salad", "Mixed greens with black beans, corn, avocado, and chipotle ranch.", 11.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Mediterranean Salad", "Cucumber, tomato, olives, feta, and lemon oregano dressing.", 10.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Spinach Salad", "Baby spinach with strawberries, goat cheese, and balsamic.", 9.99, getDrawableId("ic_food_vegetables"), "Salads");
        insertFoodItem(db, "Taco Salad", "Seasoned beef, lettuce, cheese, salsa, and sour cream in a tortilla bowl.", 12.49, getDrawableId("ic_food_tacos"), "Salads");
        insertFoodItem(db, "Chicken Caesar Wrap", "Caesar salad with grilled chicken in a flour tortilla.", 10.99, getDrawableId("ic_food_sandwich"), "Salads");
        insertFoodItem(db, "Fattoush", "Middle Eastern salad with cucumber, tomato, pita, and sumac dressing.", 9.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Arugula & Pear", "Arugula with pear, walnuts, and blue cheese vinaigrette.", 10.99, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Asian Slaw", "Shredded cabbage and carrots with sesame ginger dressing.", 7.49, getDrawableId("ic_food_coleslaw"), "Salads");
        insertFoodItem(db, "Beet & Goat Cheese", "Roasted beets, goat cheese, arugula, and balsamic glaze.", 11.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Chicken Salad", "Diced chicken with celery, grapes, and mayo on mixed greens.", 10.99, getDrawableId("ic_food_chicken"), "Salads");
        insertFoodItem(db, "Tuna Salad", "Flaked tuna with celery and mayo, served on lettuce or in a wrap.", 9.99, getDrawableId("ic_food_fish"), "Salads");
        insertFoodItem(db, "Watermelon Feta Salad", "Watermelon, feta, mint, and balsamic reduction.", 9.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Broccoli Salad", "Fresh broccoli with raisins, bacon, and creamy dressing.", 8.99, getDrawableId("ic_food_vegetables"), "Salads");
        insertFoodItem(db, "Coleslaw Salad", "Shredded cabbage and carrots with tangy coleslaw dressing.", 6.99, getDrawableId("ic_food_coleslaw"), "Salads");
        insertFoodItem(db, "Antipasto Salad", "Mixed greens with salami, olives, cheese, and Italian dressing.", 12.49, getDrawableId("ic_food_salad_bowl"), "Salads");
        insertFoodItem(db, "Edamame Salad", "Edamame, cucumber, and sesame with ginger soy dressing.", 8.49, getDrawableId("ic_food_beans"), "Salads");
        insertFoodItem(db, "Roasted Vegetable Salad", "Warm roasted vegetables over greens with balsamic.", 10.99, getDrawableId("ic_food_vegetables"), "Salads");
        insertFoodItem(db, "Summer Salad", "Mixed greens with strawberries, pecans, and poppy seed dressing.", 9.99, getDrawableId("ic_food_salad_bowl"), "Salads");
    }

    private void insertFoodItem(SQLiteDatabase db, String name, String description, double price, int imageResId, String category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, name);
        values.put(COLUMN_FOOD_DESCRIPTION, description);
        values.put(COLUMN_FOOD_PRICE, price);
        values.put(COLUMN_FOOD_IMAGE_RES_ID, imageResId);
        values.put(COLUMN_FOOD_CATEGORY, category);
        db.insert(TABLE_FOOD_ITEMS, null, values);
    }
}
