package com.example.makerlink.ui.exchange;

public class User {
    private String name;
    private String address;
    private String phone;

    // Constructor + Getters
    public User(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}
