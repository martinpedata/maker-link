package com.example.makerlink.gamification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makerlink.R;
import com.example.makerlink.navigation_pages.exchange.User;
import com.example.makerlink.playlists.PlaylistRecyclerAdapter;
import com.example.makerlink.playlists.PlaylistRecyclerModel;
import com.example.makerlink.threads.list.ThreadRecyclerActivity;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {

    public Context context;
    public ArrayList<LeaderBoardModel> users;

    public LeaderBoardAdapter(Context context, ArrayList<LeaderBoardModel> users) {
        this.context = context;
        this.users = users;
    }

    /// This is what makes the recycler view visible
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_item, parent, false);

        return new LeaderBoardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LeaderBoardModel currentUser = users.get(position);
        int placement = currentUser.getPlacement();

        String placementStr = "";
        if (placement == 1) {
            placementStr = placement + "st";
        }
        else if (placement == 2) {
            placementStr = placement + "nd";
        }
        else if (placement == 3) {
            placementStr = placement + "rd";
        }
        else {
            placementStr = placement + "th";
        }

        // Set basic info
        holder.placementText.setText(placementStr);
        holder.username.setText(currentUser.getUsername());
        holder.points.setText(String.valueOf(currentUser.getPoints()));
        if (currentUser.getBadge() != null) {
            holder.badge.setImageResource(currentUser.getBadge());
            holder.badge.setVisibility(View.VISIBLE); // optional: ensure it's shown
        } else {
            holder.badge.setImageDrawable(null); // clear the image
            holder.badge.setVisibility(View.GONE); // optional: hide if there's no badge
        }

        holder.username.getPaint().setShader(null);


        holder.placementText.setBackground(null);
        holder.points.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // default color

        switch (placement) {
            case 1: // Gold
                holder.placementText.setBackgroundResource(R.drawable.gold_gradient);
                applyTextGradient(holder.username, "#FFE680", "#D4AF37", "#B8860B");
                holder.points.setTextColor(ContextCompat.getColor(context, R.color.gold));
                break;
            case 2: // Silver
                holder.placementText.setBackgroundResource(R.drawable.silver_gradient);
                applyTextGradient(holder.username, "#DCDCDC", "#C0C0C0", "#A9A9A9");
                holder.points.setTextColor(ContextCompat.getColor(context, R.color.silver));
                break;
            case 3: // Bronze
                holder.placementText.setBackgroundResource(R.drawable.bronze_gradient);
                applyTextGradient(holder.username, "#CD7F32", "#B87333", "#8B4513");
                holder.points.setTextColor(ContextCompat.getColor(context, R.color.bronze));
                break;
        }
    }

    // Helper method to apply vertical gradient
    private void applyTextGradient(TextView textView, String color1, String color2, String color3) {
        TextPaint paint = textView.getPaint();
        float textSize = textView.getTextSize();

        Shader shader = new LinearGradient(
                0, 0, 0, textSize,
                new int[]{
                        Color.parseColor(color1),
                        Color.parseColor(color2),
                        Color.parseColor(color3)
                },
                null,
                Shader.TileMode.CLAMP
        );

        paint.setShader(shader);
        textView.invalidate(); // Force redraw
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView placementText, username, points;
        public ImageView badge;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placementText = itemView.findViewById(R.id.placementText);
            username = itemView.findViewById(R.id.nameUserLeaderboard);
            points = itemView.findViewById(R.id.pointsText);
            badge = itemView.findViewById(R.id.badge);

        }
    }
}