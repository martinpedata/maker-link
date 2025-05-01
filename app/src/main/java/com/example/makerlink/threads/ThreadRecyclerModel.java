package com.example.makerlink.threads;

public class ThreadRecyclerModel {
    private String nameThread;
    private int imageThread;
    private String authorThread;
    private String hashtagThread;
    private String dateThread;

    public ThreadRecyclerModel(String nameThread, int imageThread, String authorThread, String dateThread, String hashtagThread) {
        this.nameThread = nameThread;
        this.imageThread = imageThread;
        this.authorThread = authorThread;
        this.dateThread = dateThread;
        this.hashtagThread = hashtagThread;
    }

    public String getNameThread() {
        return nameThread;
    }

    public void setNameThread(String nameThread) {
        this.nameThread = nameThread;
    }

    public int getImageThread() {
        return imageThread;
    }

    public void setImageThread(int imageThread) {
        this.imageThread = imageThread;
    }

    public String getAuthorThread() {
        return authorThread;
    }

    public void setAuthorThread(String authorThread) {
        this.authorThread = authorThread;
    }

    public String getHashtagThread() {
        return hashtagThread;
    }

    public void setHashtagThread(String hashtagThread) {
        this.hashtagThread = hashtagThread;
    }

    public String getDateThread() {
        return dateThread;
    }

    public void setDateThread(String dateThread) {
        this.dateThread = dateThread;
    }
}
