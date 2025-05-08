package com.example.makerlink.threads;

import android.graphics.Bitmap;

public class ThreadRecyclerModel {
    private Bitmap imageThread;
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

    public ThreadRecyclerModel(int id, String nameThreadLong, String nameThreadShort, Bitmap imageThread, String dateThread, int author_id, int domain_id) {
        this.id = id;
        this.shortenedName = nameThreadShort;
        this.fullName = nameThreadLong;
        this.imageThread = imageThread;
        this.dateThread = dateThread;
        this.authorID = author_id;
        this.domainID = domain_id;
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

    public Bitmap getBitmapImage() {
        return imageThread;
    }

    public void setImageThread(Bitmap imageThread) {
        this.imageThread = imageThread;
    }


    public String getDateThread() {
        return dateThread;
    }

    public void setDateThread(String dateThread) {
        this.dateThread = dateThread;
    }
}
