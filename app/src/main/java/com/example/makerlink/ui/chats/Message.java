package com.example.makerlink.ui.chats;

public class Message {
    private String sender;
    private String message;
    private String senderId;
    public Message(String sender, String message){
        this.sender = sender;
        this.message = message;
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
}
