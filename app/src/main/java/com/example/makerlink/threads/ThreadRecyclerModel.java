package com.example.makerlink.threads;

public class ThreadRecyclerModel {
    private int imageThread;
    private String authorThread;
    private String hashtagThread;
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

    public ThreadRecyclerModel(String nameThreadLong,String nameThreadShort, int imageThread, String dateThread, int author_id, int domain_id) {
        this.shortenedName = nameThreadShort;
        this.fullName = nameThreadLong;
        this.imageThread = imageThread;
        this.dateThread = dateThread;
        this.authorID = author_id;
        this.domainID = domain_id;
    }

    public String getNameLongThread() {
        return fullName;
    }

    public String getNameShortThread() {
        return shortenedName;
    }

    public int getImageThread() {
        return imageThread;
    }

    public void setImageThread(int imageThread) {
        this.imageThread = imageThread;
    }


    public String getDateThread() {
        return dateThread;
    }

    public void setDateThread(String dateThread) {
        this.dateThread = dateThread;
    }
}
