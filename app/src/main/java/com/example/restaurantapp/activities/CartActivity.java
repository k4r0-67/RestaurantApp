package com.example.restaurantapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.CartAdapter;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.CartItem;
import com.example.restaurantapp.utils.SessionManager;

import java.util.List;
import java.util.Locale;

/**
 * CartActivity - Displays cart items with quantity controls and order summary.
 */
public class CartActivity extends AppCompatActivity {

    private static final double TAX_RATE = 0.08; // 8% tax

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private TextView tvSubtotal, tvTax, tvTotal, tvEmptyCart;
    private RecyclerView rvCart;
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.cart));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvCart = findViewById(R.id.rvCart);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        loadCartItems();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        cartItems = dbHelper.getCartItems(sessionManager.getUserId());
        adapter = new CartAdapter(this, cartItems,
                (cartItem, newQty) -> {
                    if (newQty <= 0) {
                        dbHelper.removeCartItem(cartItem.getId());
                    } else {
                        dbHelper.updateCartItemQuantity(cartItem.getId(), newQty);
                    }
                    loadCartItems();
                },
                cartItem -> {
                    dbHelper.removeCartItem(cartItem.getId());
                    Toast.makeText(this, getString(R.string.item_removed), Toast.LENGTH_SHORT).show();
                    loadCartItems();
                });
        rvCart.setAdapter(adapter);
        updateOrderSummary();
        updateEmptyState();
    }

    private void updateOrderSummary() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", subtotal));
        tvTax.setText(String.format(Locale.getDefault(), "$%.2f", tax));
        tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }

    private void updateEmptyState() {
        if (cartItems.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
            btnPlaceOrder.setEnabled(false);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            btnPlaceOrder.setEnabled(true);
        }
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.cart_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        double subtotal = 0;
        for (CartItem item : cartItems) subtotal += item.getTotalPrice();
        double total = subtotal + (subtotal * TAX_RATE);

        long orderId = dbHelper.placeOrder(sessionManager.getUserId(), cartItems, total);
        if (orderId != -1) {
            Intent intent = new Intent(this, OrderConfirmActivity.class);
            intent.putExtra(OrderConfirmActivity.EXTRA_ORDER_ID, (int) orderId);
            intent.putExtra(OrderConfirmActivity.EXTRA_TOTAL_PRICE, total);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.order_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, MenuActivity.class));
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.action_cart) {
            return true; // already here
        }
        if (id == R.id.action_orders) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
