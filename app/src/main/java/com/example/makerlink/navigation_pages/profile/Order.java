package com.example.makerlink.navigation_pages.profile;

import java.util.Calendar;

public class Order {
    private int order_number;
    private Calendar orderDate;
    private Calendar startTime;
    private Calendar endTime;
    private int rent;
    private String tool;

    public Order(int order_number, Calendar startTime, Calendar endTime, Calendar orderDate, int rent, String tool){
        this.order_number = order_number;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderDate = orderDate;
        this.rent = rent;
        this.tool = tool;
    }

    public int getOrder_number(){ return order_number; }
    public Calendar getStartTime(){ return startTime; }
    public Calendar getEndTime(){ return endTime; }
    public Calendar getOrderDate(){ return orderDate; }
    public int getRent() { return rent; }
    public String getTool(){ return tool; }
}
