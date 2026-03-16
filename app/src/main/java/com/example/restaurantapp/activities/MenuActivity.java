package com.example.restaurantapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.MenuAdapter;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.FoodItem;
import com.example.restaurantapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * MenuActivity - Displays food items filtered by the selected category.
 * Supports search/filter and allows adding items to cart.
 */
public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "extra_category";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private MenuAdapter adapter;
    private List<FoodItem> foodItems;
    private TextInputEditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        if (category == null) category = "";

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category.isEmpty() ? getString(R.string.menu) : category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Category label
        TextView tvCategory = findViewById(R.id.tvCategory);
        tvCategory.setText(category);

        // Load food items
        foodItems = category.isEmpty()
                ? dbHelper.getAllFoodItems()
                : dbHelper.getFoodItemsByCategory(category);

        // RecyclerView
        RecyclerView rvMenu = findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(this, foodItems, item -> {
            Intent intent = new Intent(MenuActivity.this, FoodDetailActivity.class);
            intent.putExtra(FoodDetailActivity.EXTRA_FOOD_ID, item.getId());
            startActivity(intent);
        }, item -> {
            dbHelper.addToCart(sessionManager.getUserId(), item.getId(), 1);
            Toast.makeText(this, item.getName() + " " + getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show();
        });
        rvMenu.setAdapter(adapter);

        // Search
        editSearch = findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Profile picture in toolbar
        View ivToolbarProfile = findViewById(R.id.ivToolbarProfile);
        if (ivToolbarProfile != null) {
            ivToolbarProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
    }

    private void filterItems(String query) {
        if (query.isEmpty()) {
            adapter.updateList(foodItems);
        } else {
            List<FoodItem> filtered = dbHelper.searchFoodItems(query);
            adapter.updateList(filtered);
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
            editSearch.requestFocus();
            return true;
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
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
