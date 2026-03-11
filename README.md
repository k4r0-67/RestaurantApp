# 🍽️ Restaurant App — Android (Java)

A complete **Android Restaurant Application** built with **Java** and **Android Studio**. The app allows users to browse a food menu, manage a cart, place orders, and view order history — all backed by a local **SQLite** database.

---

## 📱 Screenshots

> _Run the app on an emulator or device to see the UI._

| Splash | Login | Home | Menu |
|--------|-------|------|------|
| _(Splash Screen)_ | _(Login Screen)_ | _(Home Screen)_ | _(Menu Screen)_ |

---

## ✨ Features

- **Splash Screen** — Branded launch screen with auto-navigation
- **User Authentication** — Register & Login with SQLite + input validation
- **Home Screen** — Welcome message & food category grid (RecyclerView)
- **Menu Screen** — Browse items by category with search/filter
- **Food Detail Screen** — Full details, quantity selector, add to cart
- **Cart Screen** — Manage cart items, adjust quantities, view order summary
- **Order Confirmation** — Summary screen after placing an order
- **Order History** — View all past orders from the local database
- **Profile Screen** — View and edit user profile, logout
- **About Screen** — Restaurant info, contact details, and hours

---

## 🏗️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 |
| UI Framework | XML Layouts + Material Design |
| Database | SQLite (via `DatabaseHelper`) |
| Session | SharedPreferences (`SessionManager`) |
| Lists | RecyclerView + Custom Adapters |
| Build System | Gradle (Groovy DSL) |

---

## 📂 Project Structure

```
app/src/main/java/com/example/restaurantapp/
├── activities/
│   ├── SplashActivity.java
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   ├── MainActivity.java
│   ├── MenuActivity.java
│   ├── FoodDetailActivity.java
│   ├── CartActivity.java
│   ├── OrderConfirmActivity.java
│   ├── OrderHistoryActivity.java
│   ├── ProfileActivity.java
│   └── AboutActivity.java
├── adapters/
│   ├── CategoryAdapter.java
│   ├── MenuAdapter.java
│   ├── CartAdapter.java
│   └── OrderHistoryAdapter.java
├── models/
│   ├── User.java
│   ├── FoodItem.java
│   ├── CartItem.java
│   └── Order.java
├── database/
│   └── DatabaseHelper.java
└── utils/
    └── SessionManager.java
```

---

## 🗄️ Database

The app uses **SQLite** with the following tables:

- `users` — Registered users
- `food_items` — Pre-populated menu items (Appetizers, Main Course, Desserts, Beverages)
- `cart_items` — Current cart contents per user
- `orders` — Placed orders
- `order_items` — Items linked to each order

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Hedgehog or newer recommended)
- JDK 8+
- Android SDK with API 24–34

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/k4r0-67/RestaurantApp.git
   ```

2. **Open in Android Studio:**
   - Open Android Studio → **File → Open** → select the `RestaurantApp` folder

3. **Sync Gradle:**
   - Android Studio will prompt to sync Gradle. Click **Sync Now**.

4. **Run the app:**
   - Select an emulator (API 24+) or connect a physical device
   - Click the **Run ▶** button

---

## 📋 Sample Menu Data

The app pre-populates the database with sample items:

| Category | Items |
|----------|-------|
| 🥗 Appetizers | Spring Rolls, Chicken Soup, Bruschetta, Mozzarella Sticks, Chicken Wings |
| 🍽️ Main Course | Grilled Chicken, Spaghetti Carbonara, Ribeye Steak, Classic Burger, Margherita Pizza, Salmon Fillet, Veggie Stir Fry |
| 🍰 Desserts | Chocolate Lava Cake, Vanilla Ice Cream, Tiramisu, Cheesecake, Crème Brûlée |
| 🥤 Beverages | Espresso, Fresh Orange Juice, Mango Smoothie, Lemonade, Cappuccino |

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).