package com.example.makerlink.navigation_pages.exchange;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class User {
    private String name;
    private String address;
    private String phone;
    private int rent;

    private String tool;
    private String description;
    private int startday;
    private int endday;
    private Bitmap imageThreadBitmap;
    private String lenderImage;

    // Constructor + Getters
    public User(String name, String address, int rent, String tool, String description, int start, int end, String lenderImage) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
        this.description = description;
        this.startday = start;
        this.endday = end;
        this.imageThreadBitmap = base64ToBitMap(lenderImage);
        this.lenderImage = lenderImage;
    }
    public User(String name, String address, int rent, String tool) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
    }
    public String getName() { return name; }
    public String getAddress() { return address; }

    public int getRent(){ return rent; }

    public String getTool(){ return tool; }
    public String getDescription(){ return description; }

    public int getStartday() { return startday; }

    public int getEndday() { return endday; }
    public Bitmap getUserImage(){ return imageThreadBitmap; }
    public String get64(){ return lenderImage; }
    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.NO_WRAP );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}
