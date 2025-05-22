package com.example.makerlink.gamification;

import android.widget.ImageView;

public class LeaderBoardModel {
    private String points;
    private Integer badge;
    private int placement;
    private String username;

    public LeaderBoardModel(int points, Integer badge, int placement, String username) {
        this.points = points + " points";
        this.badge = badge;
        this.placement = placement;
        this.username = username;
    }

    public String getPoints() {
        return points;
    }

    public Integer getBadge() {
        return badge;
    }

    public int getPlacement() {
        return placement;
    }

    public String getUsername() {
        return username;
    }
}
