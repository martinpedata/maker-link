package com.example.makerlink.navigation_pages.exchange;

public class User {
    private String name;
    private String address;
    private String phone;
    private int rent;

    private String tool;
    private String description;
    private int startday;
    private int endday;

    // Constructor + Getters
    public User(String name, String address, int rent, String tool, String description, int start, int end) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.tool = tool;
        this.description = description;
        this.startday = start;
        this.endday = end;
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

    public int getStartday() { return startday; }

    public int getEndday() { return endday; }
}
