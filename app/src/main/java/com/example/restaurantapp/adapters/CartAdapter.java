package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.models.CartItem;

import java.util.List;
import java.util.Locale;

/**
 * CartAdapter - RecyclerView adapter for displaying items in the user's cart.
 * Supports quantity adjustment and item removal.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnQuantityChangedListener {
        void onQuantityChanged(CartItem item, int newQuantity);
    }

    public interface OnRemoveItemListener {
        void onRemoveItem(CartItem item);
    }

    private final Context context;
    private final List<CartItem> cartItems;
    private final OnQuantityChangedListener quantityChangedListener;
    private final OnRemoveItemListener removeItemListener;

    public CartAdapter(Context context, List<CartItem> cartItems,
                       OnQuantityChangedListener quantityChangedListener,
                       OnRemoveItemListener removeItemListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.quantityChangedListener = quantityChangedListener;
        this.removeItemListener = removeItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.ivFood.setImageResource(R.drawable.ic_food_placeholder);
        holder.tvName.setText(item.getFoodItem().getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getFoodItem().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvItemTotal.setText(String.format(Locale.getDefault(), "$%.2f", item.getTotalPrice()));

        holder.btnDecrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            quantityChangedListener.onQuantityChanged(item, newQty);
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            quantityChangedListener.onQuantityChanged(item, newQty);
        });

        holder.btnRemove.setOnClickListener(v -> removeItemListener.onRemoveItem(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvPrice, tvQuantity, tvItemTotal;
        Button btnDecrease, btnIncrease, btnRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.ivFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
