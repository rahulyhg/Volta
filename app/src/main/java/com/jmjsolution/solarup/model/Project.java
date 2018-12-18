package com.jmjsolution.solarup.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Project {

    private String mTitle, mLocation;
    private String mDate;
    private String mBigDate;

    public Project(String title, String location, String date, String bigDate) {
        mTitle = title;
        mLocation = location;
        mDate = date;
        mBigDate = bigDate;
    }

    public String getBigDate() {
        return mBigDate;
    }

    public void setBigDate(String bigDate) {
        mBigDate = bigDate;
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
