package com.example.restaurantapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.activities.MenuActivity;

import java.util.List;

/**
 * CategoryAdapter - RecyclerView adapter for displaying food categories on the home screen.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<String> categories;

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.tvCategory.setText(category);
        holder.ivCategory.setImageResource(getCategoryIconRes(category));

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MenuActivity.class);
            intent.putExtra(MenuActivity.EXTRA_CATEGORY, category);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Returns the drawable resource for the given category name.
     */
    private int getCategoryIconRes(String category) {
        switch (category) {
            case "Appetizers": return R.drawable.ic_category_appetizers;
            case "Main Course": return R.drawable.ic_category_main_course;
            case "Desserts": return R.drawable.ic_category_desserts;
            case "Beverages": return R.drawable.ic_category_beverages;
            default: return R.drawable.ic_food_placeholder;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCategory;
        ImageView ivCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivCategory = itemView.findViewById(R.id.ivCategory);
        }
    }
}
