package com.example.makerlink.navigation_pages.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private List<Order> orderList;


    public Context context;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    // ViewHolder class to hold reference to views for each item
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView order_nr, starttime, endtime, tool, rent;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            order_nr = itemView.findViewById(R.id.order_number);
            starttime = itemView.findViewById(R.id.order_datetime);
            endtime = itemView.findViewById(R.id.estimated_delivery);
            tool = itemView.findViewById(R.id.tool);
            rent = itemView.findViewById(R.id.price);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order chat = orderList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String order_id = "Order#: " + String.valueOf(chat.getOrder_number());
        holder.order_nr.setText(order_id);
        holder.starttime.setText(sdf.format(chat.getOrderDate().getTime()));
        String timeRange = "Time of occupation: " + sdf.format(chat.getStartTime().getTime()) + " - " + sdf.format(chat.getEndTime().getTime());
        holder.endtime.setText(timeRange);
        holder.tool.setText(chat.getTool());
        holder.rent.setText(" " +chat.getRent()+"€/hour");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}