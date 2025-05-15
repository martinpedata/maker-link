package com.example.makerlink.navigation_pages.exchange;

import java.util.Calendar;

public class Reservation {
    Calendar startTime;
    Calendar endTime;

    public Reservation(Calendar start, Calendar end) {
        this.startTime = start;
        this.endTime = end;
    }
}
