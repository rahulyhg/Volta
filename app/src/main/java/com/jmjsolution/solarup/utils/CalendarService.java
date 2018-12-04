package com.jmjsolution.solarup.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import com.jmjsolution.solarup.model.CalendarEvent;

import static android.content.Context.MODE_PRIVATE;

public class CalendarService {

    public static final String MY_PREFS_NAME = "VoltaPrefFile";
    public static ArrayList<CalendarEvent> mEventsList;
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };


    public static void readCalendar(Context context) {
        readCalendar(context, 1, 0);
    }

    public static void readCalendar(Context context, int days, int hours) {

        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if(sharedPref.getBoolean("isEmailConfigured", false)){
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
            String[] selectionArgs = new String[] {sharedPref.getString("email", null)};
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;

            @SuppressLint("MissingPermission") Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            HashSet<String> calendarIds = getCalenderIds(cursor);
            HashMap<String, List<CalendarEvent>> eventMap = new HashMap<>();

            for (String id : calendarIds) {

                Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                long now = new Date().getTime();

                ContentUris.appendId(builder, now);
                ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * days) + (DateUtils.HOUR_IN_MILLIS * hours));

                Cursor eventCursor = contentResolver.query(builder.build(), new String[] { "title", "begin", "end", "allDay"}, CalendarContract.Events.CALENDAR_ID+"=" + id, null, "startDay ASC, startMinute ASC");

                System.out.println("eventCursor count="+eventCursor.getCount());

                if(eventCursor.getCount()>0) {

                    mEventsList = new ArrayList<>();
                    eventCursor.moveToFirst();
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


    }

    public static void syncCalendars(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(null);
        if(accounts.length != 0 && !sharedPref.getBoolean("isEmailConfigured", false)){
            determineCalendar(context, sharedPref.getString("email", null));
        }
        String authority = CalendarContract.Calendars.CONTENT_URI.getAuthority();
        for(Account account : accounts){
            if(account.name.equals(sharedPref.getString("email", null))) {
                Bundle extras = new Bundle();
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, authority, extras);
            }
        }
    }

    private static CalendarEvent loadEvent(Cursor csr) {
        return new CalendarEvent(csr.getString(0), new Date(csr.getLong(1)), new Date(csr.getLong(2)), !csr.getString(3).equals("0"));
    }

    private static HashSet<String> getCalenderIds(Cursor cursor) {

        HashSet<String> calendarIds = new HashSet<String>();

        try {

            if(cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);

                }
            }
        }

        catch(AssertionError | Exception ex) {
            ex.printStackTrace();
        }

        return calendarIds;

    }

    @SuppressLint("MissingPermission")
    public static boolean determineCalendar(Context context, String accountName) {

        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
        String[] selectionArgs = new String[] {accountName};
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        if(cursor != null){
            Toast.makeText(context, "Votre compte est maintenant lié.", Toast.LENGTH_SHORT).show();
            editor.putString("email", accountName);
            editor.putBoolean("isEmailConfigured", true);
            editor.apply();
            return true;
        }

        Toast.makeText(context, "Veuillez s'il vous plait vous connectez a votre compte.", Toast.LENGTH_SHORT).show();
        editor.putBoolean("isEmailConfigured", false);
        editor.apply();
        return false;

    }

    @SuppressLint("MissingPermission")
    public static void other(Context context, String accountName) {

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
        String[] selectionArgs = new String[] {accountName};
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cursor.moveToNext()) {
            final String _id = cursor.getString(0);
            final String displayName = cursor.getString(1);

            String[] calendarID = new String[0];
            if (displayName.equals("ryerson.ca")) {
                calendarID = new String[]{_id};
            }

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance(Locale.FRANCE);
            beginTime.set(2014, Calendar.SEPTEMBER, 2, 8, 0);
            long startMills = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance(Locale.FRANCE);
            endTime.set(2014, Calendar.SEPTEMBER, 2, 20, 0);
            long endMills = endTime.getTimeInMillis();

            ContentUris.appendId(builder, startMills);
            ContentUris.appendId(builder, endMills);

            Cursor eventCursor = contentResolver.query(builder.build(), new String[]{CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.DESCRIPTION},
                    CalendarContract.Instances.CALENDAR_ID + " = ?", calendarID, null);

            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final String description = eventCursor.getString(3);

                Log.d("Cursor", "Title: " + title + "\tDescription: " + description + "\tBegin: " + begin + "\tEnd: " + end);
            }
        }
    }

}