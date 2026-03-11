package com.example.restaurantapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.OrderHistoryAdapter;
import com.example.restaurantapp.database.DatabaseHelper;
import com.example.restaurantapp.models.Order;
import com.example.restaurantapp.utils.SessionManager;

import java.util.List;

/**
 * OrderHistoryActivity - Displays past orders for the logged-in user.
 */
public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.order_history));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SessionManager sessionManager = new SessionManager(this);

        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        List<Order> orders = dbHelper.getOrderHistory(sessionManager.getUserId());
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(new OrderHistoryAdapter(this, orders));

        if (orders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
