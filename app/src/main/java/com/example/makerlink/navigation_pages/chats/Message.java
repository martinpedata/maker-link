package com.example.makerlink.navigation_pages.chats;

public class Message {
    private String sender;
    private String message;
    private String senderId;
    private String timestamp;
    private boolean isDateHeader;
    public Message(String sender, String message, String timestamp){
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.isDateHeader = false;
    }
    public Message(String date) {
        this.message = date;
        this.isDateHeader = true;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
    public String getSenderId(){
        return senderId;
    }
    public boolean isDateHeader() {
        return isDateHeader;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
