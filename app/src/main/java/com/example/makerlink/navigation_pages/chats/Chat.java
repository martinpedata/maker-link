package com.example.makerlink.navigation_pages.chats;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Chat {
    private String name;
    private int id;
    private Bitmap imagebitmap;
    private String image;
    public Chat(String name, int id, String image){
        this.name = name;
        this.id = id;
        this.imagebitmap = base64ToBitMap(image);
        this.image = image;
    }

    public String getName(){
        return name;
    }

    public int getId() {
        return id;
    }
    public Bitmap getImagebitmap(){ return imagebitmap; }

    public String getImage(){ return image; }
    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.NO_WRAP );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}
