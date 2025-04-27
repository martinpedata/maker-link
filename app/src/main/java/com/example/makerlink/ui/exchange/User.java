package com.example.makerlink.ui.exchange;

public class User {
    private String name;
    private String address;
    private String phone;
    private int rent;

    // Constructor + Getters
    public User(String name, String address, String phone, int rent) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.rent = rent;
    }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public int getRent(){ return rent; }
}
