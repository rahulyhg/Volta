package com.jmjsolution.solarup.model;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Project implements Comparable<Project>{

    private String mTitle, mLocation;
    private String mDate;
    private String mBigDate;
    private long mLongDate;

    public Project(String title, String location, String date, String bigDate, long longDate) {
        mTitle = title;
        mLocation = location;
        mDate = date;
        mBigDate = bigDate;
        mLongDate = longDate;
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


    public Date getLongDate() {
        return new Date(mDate);
    }

    public void setLongDate(long longDate) {
        mLongDate = longDate;
    }

    @Override
    public int compareTo(@NonNull Project project) {
        return getLongDate().compareTo(project.getLongDate());
    }
}
