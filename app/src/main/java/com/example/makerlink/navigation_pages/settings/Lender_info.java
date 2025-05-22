package com.example.makerlink.navigation_pages.settings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Lender_info {
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
    private int location_id;

    // Constructor + Getters
    public Lender_info(int location_id,String name, String address, int rent, String tool, String description, int start, int end, String lenderImage) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
        this.description = description;
        this.startday = start;
        this.endday = end;
        this.imageThreadBitmap = base64ToBitMap(lenderImage);
        this.location_id = location_id;
        this.lenderImage = lenderImage;
    }
    public Lender_info(String name, String address, int rent, String tool) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
    }
    public String getLenderName() { return name; }
    public String getLenderAddress() { return address; }

    public int getLenderRent(){ return rent; }

    public String getLenderTool(){ return tool; }
    public String getLenderDescription(){ return description; }

    public int getLenderStartday() { return startday; }

    public int getLenderEndday() { return endday; }
    public Bitmap getLenderImage(){ return imageThreadBitmap; }
    public String getbase64(){ return lenderImage; }
    public Integer getLocationID(){ return location_id; }
    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.NO_WRAP );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}
