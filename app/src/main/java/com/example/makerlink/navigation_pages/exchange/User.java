package com.example.makerlink.navigation_pages.exchange;

public class User {
    private String name;
    private String address;
    private String phone;
    private int rent;

    private String tool;
    private String description;

    // Constructor + Getters
    public User(String name, String address, int rent, String tool, String description) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
        this.description = description;
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
}
