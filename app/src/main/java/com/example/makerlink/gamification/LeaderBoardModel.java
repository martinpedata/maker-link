package com.example.makerlink.gamification;

import android.widget.ImageView;

public class LeaderBoardModel {
    private String points;
    private int badge;
    private String placement;
    private String username;

    public LeaderBoardModel(int points, int badge, int placement, String username) {
        this.points = points + " points";
        this.badge = badge;
        if (placement == 1) {
            this.placement = placement + "st";
        }
        else if (placement == 2) {
            this.placement = placement + "nd";
        }
        else if (placement == 3) {
            this.placement = placement + "rd";
        }
        else {
            this.placement = placement + "th";
        }
        this.username = username;
    }

    public String getPoints() {
        return points;
    }

    public int getBadge() {
        return badge;
    }

    public String getPlacement() {
        return placement;
    }

    public String getUsername() {
        return username;
    }
}
