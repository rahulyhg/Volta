package com.jmjsolution.solarup.model;

import java.sql.Timestamp;
import java.util.Date;

public class CalendarEvent implements Comparable<CalendarEvent>{

    private String title;
    private String location;
    private Date begin, end;
    private boolean allDay;
    private long id;
    private int eventStatus;
    private boolean isFromGmail;

    public CalendarEvent() { }

    public CalendarEvent(String title, Date begin, Date end, boolean allDay, long id, String location, int eventStatus, boolean isFromGmail) {
        setTitle(title);
        setBegin(begin);
        setEnd(end);
        setAllDay(allDay);
        setId(id);
        setLocation(location);
        setEventStatus(eventStatus);
        setIsFromGmail(isFromGmail);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public boolean getIsFromGmail() {
        return isFromGmail;
    }

    public void setIsFromGmail(boolean fromGmail) {
        isFromGmail = fromGmail;
    }


    @Override
    public String toString(){
        return getTitle() + " " + getBegin() + " " + getEnd() + " " + isAllDay();
    }

    @Override
    public int compareTo(CalendarEvent other) {
        // -1 = less, 0 = equal, 1 = greater
        return getBegin().compareTo(other.begin);
    }
}
