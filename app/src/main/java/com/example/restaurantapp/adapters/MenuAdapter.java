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
import com.example.restaurantapp.models.FoodItem;

import java.util.List;
import java.util.Locale;

/**
 * MenuAdapter - RecyclerView adapter for displaying food menu items.
 * Supports click to view details and add-to-cart action.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FoodItem item);
    }

    public interface OnAddToCartListener {
        void onAddToCart(FoodItem item);
    }

    private final Context context;
    private List<FoodItem> foodItems;
    private final OnItemClickListener clickListener;
    private final OnAddToCartListener addToCartListener;

    public MenuAdapter(Context context, List<FoodItem> foodItems,
                       OnItemClickListener clickListener,
                       OnAddToCartListener addToCartListener) {
        this.context = context;
        this.foodItems = foodItems;
        this.clickListener = clickListener;
        this.addToCartListener = addToCartListener;
    }

    /**
     * Update the list of food items (used for search/filter).
     */
    public void updateList(List<FoodItem> newList) {
        this.foodItems = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);

        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));
        int imageResId = item.getImageResId();
        holder.ivFood.setImageResource(imageResId != 0 ? imageResId : R.drawable.ic_food_placeholder);

        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
        holder.btnAddToCart.setOnClickListener(v -> addToCartListener.onAddToCart(item));
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvDescription, tvPrice;
        Button btnAddToCart;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.ivFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
