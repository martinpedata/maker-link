package com.example.makerlink.playlists;

import android.graphics.Bitmap;

import com.example.makerlink.R;

public class PlaylistRecyclerModel {
    private Bitmap imagePlaylist;
    private int playlist_id;
    private int privacy;
    private String name;
    private int authorID;

    public PlaylistRecyclerModel(int playlist_id, int privacy, String name, int authorID) {
        this.playlist_id = playlist_id;
        this.privacy = privacy;
        this.name = name;
        this.authorID = authorID;
    }



    public int getPlaylistID() {
        return playlist_id;
    }
    public int getAuthorID() {
        return authorID;
    }

    public Bitmap getImagePlaylist() {
        return imagePlaylist;
    }

    public int imageResourcePrivacy() {
        if (getPrivacy() == 1) {
            return R.drawable.lock_closed;
        }
        else {
            return R.drawable.lock_open;
        }
    }
    public String getPrivacyString() {
        if (getPrivacy() == 1) {
            return "Private";
        }
        else {
            return "Public";
        }
    }

    public int getPrivacy() {
        return privacy;
    }

    public String getName() {
        return name;
    }
}
