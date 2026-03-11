package com.example.restaurantapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapp.R;

import java.util.Locale;

/**
 * OrderConfirmActivity - Displayed after a successful order placement.
 * Shows the order number, total, and estimated delivery time.
 */
public class OrderConfirmActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_TOTAL_PRICE = "extra_total_price";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        int orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, 0);
        double totalPrice = getIntent().getDoubleExtra(EXTRA_TOTAL_PRICE, 0.0);

        TextView tvOrderNumber = findViewById(R.id.tvOrderNumber);
        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvEstimatedTime = findViewById(R.id.tvEstimatedTime);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        tvOrderNumber.setText(getString(R.string.order_number, orderId));
        tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", totalPrice));
        tvEstimatedTime.setText(getString(R.string.estimated_delivery));

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
