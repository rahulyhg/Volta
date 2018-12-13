package com.jmjsolution.solarup.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Project {

    private String mTitle, mLocation;
    private String mDate;


    public Project(String title, String location, String date) {
        mTitle = title;
        mLocation = location;
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }


}
