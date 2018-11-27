package com.jmjsolution.solarup.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;


public class CalendarService {

    public static ArrayList<CalendarEvent> mEventsList;

    // Default constructor
    public static void readCalendar(Context context) {
        readCalendar(context, 1, 0);
    }

    // Use to specify specific the time span
    public static void readCalendar(Context context, int days, int hours) {

        ContentResolver contentResolver = context.getContentResolver();

        /* Create a cursor and read from the calendar (for Android API below 4.0)
        final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[] { "_id", "displayName", "selected" }), null, null, null);*/


         // Use the cursor below for Android API 4.0+ (Thanks to SLEEPLisNight)

          Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
          new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" },
          null, null, null);


        HashSet<String> calendarIds = getCalenderIds(cursor);
        HashMap<String, List<CalendarEvent>> eventMap = new HashMap<String, List<CalendarEvent>>();

        for (String id : calendarIds) {

            // Create a builder to define the time span
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - (DateUtils.DAY_IN_MILLIS * days) - (DateUtils.HOUR_IN_MILLIS * hours));
            ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * days) + (DateUtils.HOUR_IN_MILLIS * hours));

            // Create an event cursor to find all events in the calendar
            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[] { "title", "begin", "end", "allDay"}, CalendarContract.Events.CALENDAR_ID+"=" + id, null, "startDay ASC, startMinute ASC");

            System.out.println("eventCursor count="+eventCursor.getCount());

            // If there are actual events in the current calendar, the count will exceed zero
            if(eventCursor.getCount()>0) {

                // Create a list of calendar events for the specific calendar
                mEventsList = new ArrayList<CalendarEvent>();
                eventCursor.moveToFirst();
                // Create an object of CalendarEvent which contains the title, when the event begins and ends,
                // and if it is a full day event or not
                CalendarEvent ce = loadEvent(eventCursor);
                mEventsList.add(ce);
                System.out.println(ce.toString());

                while (eventCursor.moveToNext()) {

                    ce = loadEvent(eventCursor);
                    mEventsList.add(ce);
                    System.out.println(ce.toString());
                }

                Collections.sort(mEventsList);
                eventMap.put(id, mEventsList);

                System.out.println(eventMap.keySet().size() + " " + eventMap.values());

            }
        }
    }

    // Returns a new instance of the calendar object
    private static CalendarEvent loadEvent(Cursor csr) {
        return new CalendarEvent(csr.getString(0), new Date(csr.getLong(1)), new Date(csr.getLong(2)), !csr.getString(3).equals("0"));
    }

    // Creates the list of calendar ids and returns it in a set
    private static HashSet<String> getCalenderIds(Cursor cursor) {

        HashSet<String> calendarIds = new HashSet<String>();

        try
        {

            // If there are more than 0 calendars, continue
            if(cursor.getCount() > 0)
            {

                // Loop to set the id for all of the calendars
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);

                }
            }
        }

        catch(AssertionError ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return calendarIds;

    }
}