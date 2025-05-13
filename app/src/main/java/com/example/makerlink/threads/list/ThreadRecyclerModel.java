package com.example.makerlink.threads.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ThreadRecyclerModel {
    private String imageThread;
    private Bitmap imageThreadBitmap;
    private String threadDocument;
    private int id;
    private String shortenedName;
    private String fullName;
    private String dateThread;
    private int authorID;
    private int domainID;

    public int getAuthorID() {
        return authorID;
    }

    public int getDomainID() {
        return domainID;
    }

    public ThreadRecyclerModel(int id, String nameThreadLong, String nameThreadShort, String imageThread, String dateThread, int author_id, int domain_id, String threadDocument) {
        this.id = id;
        this.threadDocument = threadDocument;
        this.shortenedName = nameThreadShort;
        this.fullName = nameThreadLong;
        this.imageThread = imageThread;
        this.dateThread = dateThread;
        this.authorID = author_id;
        this.domainID = domain_id;

        imageThreadBitmap =  base64ToBitMap(imageThread); //The b64 is converted to Bitmap in the helper method below
    }

    public String getThreadDocument() {
        return threadDocument;
    }

    public int getID() {
        return id;
    }

    public String getNameLongThread() {
        return fullName;
    }

    public String getNameShortThread() {
        return shortenedName;
    }

    public String getBase64Image() {
        return imageThread;
    }

    public Bitmap getBitmapImage() {
        return imageThreadBitmap;
    }


    public String getDateThread() {
        return dateThread;
    }

    public void setDateThread(String dateThread) {
        this.dateThread = dateThread;
    }

    public Bitmap base64ToBitMap(String b64String){
        byte[] imageBytes = Base64.decode( b64String, Base64.DEFAULT );
        Bitmap bitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );
        return bitmap;
    }
}

