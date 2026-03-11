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

    // Database info
    private static final String DATABASE_NAME = "RestaurantApp.db";
    private static final int DATABASE_VERSION = 1;

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
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
        insertFoodItem(db, "Spring Rolls", "Crispy fried rolls filled with vegetables and served with sweet chili sauce.", 6.99, 0, "Appetizers");
        insertFoodItem(db, "Chicken Soup", "Hearty homemade chicken soup with vegetables and herbs.", 5.49, 0, "Appetizers");
        insertFoodItem(db, "Bruschetta", "Toasted bread topped with fresh tomatoes, garlic, basil, and olive oil.", 7.99, 0, "Appetizers");
        insertFoodItem(db, "Mozzarella Sticks", "Golden-fried mozzarella sticks served with marinara sauce.", 8.49, 0, "Appetizers");
        insertFoodItem(db, "Chicken Wings", "Crispy chicken wings tossed in your choice of buffalo or BBQ sauce.", 11.99, 0, "Appetizers");

        insertFoodItem(db, "Grilled Chicken", "Juicy grilled chicken breast with herbs, served with seasonal vegetables.", 14.99, 0, "Main Course");
        insertFoodItem(db, "Spaghetti Carbonara", "Classic Italian pasta with creamy egg sauce, pancetta, and Parmesan.", 13.99, 0, "Main Course");
        insertFoodItem(db, "Ribeye Steak", "Premium 12oz ribeye steak cooked to your preference with garlic butter.", 28.99, 0, "Main Course");
        insertFoodItem(db, "Classic Burger", "Beef patty with lettuce, tomato, cheese, and special sauce on a brioche bun.", 12.99, 0, "Main Course");
        insertFoodItem(db, "Margherita Pizza", "Classic pizza with fresh mozzarella, tomato sauce, and basil.", 14.49, 0, "Main Course");
        insertFoodItem(db, "Salmon Fillet", "Pan-seared salmon with lemon butter sauce and asparagus.", 19.99, 0, "Main Course");
        insertFoodItem(db, "Veggie Stir Fry", "Fresh seasonal vegetables stir-fried in savory sauce, served over rice.", 11.99, 0, "Main Course");

        insertFoodItem(db, "Chocolate Lava Cake", "Warm chocolate cake with a gooey molten center, served with vanilla ice cream.", 7.99, 0, "Desserts");
        insertFoodItem(db, "Vanilla Ice Cream", "Three scoops of premium vanilla ice cream with your choice of toppings.", 5.49, 0, "Desserts");
        insertFoodItem(db, "Tiramisu", "Classic Italian dessert with espresso-soaked ladyfingers and mascarpone cream.", 8.49, 0, "Desserts");
        insertFoodItem(db, "Cheesecake", "New York-style cheesecake with strawberry topping on a graham cracker crust.", 7.49, 0, "Desserts");
        insertFoodItem(db, "Crème Brûlée", "Classic French vanilla custard with a caramelized sugar topping.", 8.99, 0, "Desserts");

        insertFoodItem(db, "Espresso", "Rich and bold single or double shot of espresso.", 2.99, 0, "Beverages");
        insertFoodItem(db, "Fresh Orange Juice", "Freshly squeezed orange juice, served chilled.", 4.49, 0, "Beverages");
        insertFoodItem(db, "Mango Smoothie", "Creamy blended mango smoothie with a hint of lime.", 5.99, 0, "Beverages");
        insertFoodItem(db, "Lemonade", "Freshly made lemonade with mint, served over ice.", 3.99, 0, "Beverages");
        insertFoodItem(db, "Cappuccino", "Espresso with steamed milk foam, optionally dusted with cocoa.", 4.49, 0, "Beverages");
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
