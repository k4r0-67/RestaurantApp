package com.example.restaurantapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.restaurantapp.R;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.FoodItem;
import com.example.restaurantapp.utils.SessionManager;

import java.util.Locale;

/**
 * FoodDetailActivity - Shows full details of a food item.
 * Includes quantity selector and an "Add to Cart" button.
 */
public class FoodDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD_ID = "extra_food_id";
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 20;

    private int quantity = 1;
    private FoodItem foodItem;
    private TextView tvQuantity;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        int foodId = getIntent().getIntExtra(EXTRA_FOOD_ID, -1);
        if (foodId == -1) {
            finish();
            return;
        }

        foodItem = dbHelper.getFoodItemById(foodId);
        if (foodItem == null) {
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(foodItem.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Populate UI
        ImageView ivFood = findViewById(R.id.ivFood);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        Button btnDecrease = findViewById(R.id.btnDecrease);
        Button btnIncrease = findViewById(R.id.btnIncrease);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        int imageResId = foodItem.getImageResId();
        ivFood.setImageResource(imageResId != 0 ? imageResId : R.drawable.ic_food_placeholder);
        tvName.setText(foodItem.getName());
        tvDescription.setText(foodItem.getDescription());
        tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", foodItem.getPrice()));
        tvQuantity.setText(String.valueOf(quantity));

        btnDecrease.setOnClickListener(v -> {
            if (quantity > MIN_QUANTITY) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < MAX_QUANTITY) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            dbHelper.addToCart(sessionManager.getUserId(), foodItem.getId(), quantity);
            Toast.makeText(this,
                    quantity + "x " + foodItem.getName() + " " + getString(R.string.added_to_cart),
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
