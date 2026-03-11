package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.R;
import com.example.restaurantapp.models.CartItem;
import com.example.restaurantapp.models.Order;

import java.util.List;
import java.util.Locale;

/**
 * OrderHistoryAdapter - RecyclerView adapter for displaying past orders.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orders;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText(context.getString(R.string.order_number, order.getId()));
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTotal.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalPrice()));

        // Build items summary
        StringBuilder itemsSummary = new StringBuilder();
        List<CartItem> items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            itemsSummary.append(item.getQuantity())
                    .append("x ")
                    .append(item.getFoodItem().getName());
            if (i < items.size() - 1) itemsSummary.append(", ");
        }
        holder.tvOrderItems.setText(itemsSummary.toString());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal, tvOrderItems;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
        }
    }
}
