package com.example.makerlink.navigation_pages.chats;

public class Chat {
    private String name;
    private int id;
    public Chat(String name, int id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getId() {
        return id;
    }
}
