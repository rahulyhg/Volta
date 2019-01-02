package com.jmjsolution.solarup.services.calendarService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.text.format.DateUtils;

import com.jmjsolution.solarup.model.CalendarEvent;

import static android.content.Context.MODE_PRIVATE;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;

public class CalendarService {

    public static final String MY_PREFS_NAME = "VoltaPrefFile";
    public static ArrayList<CalendarEvent> mEventsList;
    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    private static final String[] attendeeProjection = new String[]{
            Attendees._ID,
            Attendees.EVENT_ID,
            Attendees.ATTENDEE_NAME,
            Attendees.ATTENDEE_EMAIL,
            Attendees.ATTENDEE_TYPE,
            Attendees.ATTENDEE_RELATIONSHIP,
            Attendees.ATTENDEE_STATUS
    };

    public static void readCalendar(Context context) {
        readCalendar(context, 1, 0);
    }

    public static void readCalendar(Context context, int days, int hours) {

        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if(sharedPref.getBoolean(IS_EMAIL_LINKED, false)) {
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
            String[] selectionArgs = new String[]{sharedPref.getString(GMAIL, null)};
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;

            @SuppressLint("MissingPermission") Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            HashSet<String> calendarIds = getCalenderIds(cursor);
            HashMap<String, List<CalendarEvent>> eventMap = new HashMap<>();

            long idCal = getGoogleCalendarId(context);

            for (String id : calendarIds) {

                if (id.equalsIgnoreCase(String.valueOf(idCal))) {

                    Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                    long now = new Date().getTime();

                    ContentUris.appendId(builder, now);
                    ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * days) + (DateUtils.HOUR_IN_MILLIS * hours));

                    Cursor eventCursor = contentResolver.query(builder.build(), new String[]{
                            CalendarContract.Events.TITLE,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Events.ALL_DAY,
                            CalendarContract.Instances.EVENT_ID,
                            CalendarContract.Events.EVENT_LOCATION,
                            CalendarContract.Events.SELF_ATTENDEE_STATUS},
                            CalendarContract.Events.CALENDAR_ID + "=" + id,
                            null,
                            "startDay ASC, startMinute ASC");

                    System.out.println("eventCursor count=" + Objects.requireNonNull(eventCursor).getCount());

                    if (eventCursor.getCount() > 0) {

                        mEventsList = new ArrayList<>();
                        eventCursor.moveToFirst();
                        CalendarEvent ce = loadEvent(eventCursor);

                        ce.setEventStatus(getAttendeesStatus(contentResolver, ce, context));
                        mEventsList.add(ce);
                        System.out.println(ce.toString());

                        while (eventCursor.moveToNext()) {

                            ce = loadEvent(eventCursor);
                            ce.setEventStatus(getAttendeesStatus(contentResolver, ce, context));
                            mEventsList.add(ce);
                            }

                        Collections.sort(mEventsList);
                        eventMap.put(id, mEventsList);

                    }
                }
            }
        }


    }

    private static int getAttendeesStatus(ContentResolver contentResolver, CalendarEvent ce, Context context) {
        int status = 1;
        final String query = "(" + Attendees.EVENT_ID + " = ?)";
        final String[] args = new String[]{String.valueOf(ce.getId())};
        @SuppressLint({"MissingPermission", "Recycle"}) final Cursor cursoir = contentResolver.query(Attendees.CONTENT_URI, attendeeProjection, query, args, null);
        Objects.requireNonNull(cursoir).moveToFirst();

        if(cursoir.getCount() > 0) {

            String mail = cursoir.getString(3);

            if (mail.equalsIgnoreCase(context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(GMAIL, null))) {
                status = cursoir.getInt(6);
            }

            while (cursoir.moveToNext()) {
                mail = cursoir.getString(3);
                if (mail.equalsIgnoreCase(context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).getString(GMAIL, null))) {
                    status = cursoir.getInt(6);
                }

            }
        }

        return status;
    }

    public static void inviteAttendees(Context context, long eventID, String mail){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Attendees.ATTENDEE_EMAIL, mail);
        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
        values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);
        values.put(Attendees.EVENT_ID, eventID);
        @SuppressLint("MissingPermission") Uri uri = contentResolver.insert(Attendees.CONTENT_URI, values);
    }

    private static long getGoogleCalendarId(Context context) {

        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.OWNER_ACCOUNT};

        @SuppressLint("MissingPermission")
        Cursor calCursor = Objects.requireNonNull(context.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                CalendarContract.Calendars._ID + " ASC"));

        assert calCursor != null;
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);

                String email = Objects.requireNonNull(context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null));
                if (displayName.contains(email)) {
                    return id;
                }

            } while (calCursor.moveToNext());
        }
        calCursor.close();
        return -1;

    }

    public static void syncCalendars(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(null);
        if(accounts.length != 0 && !sharedPref.getBoolean(IS_EMAIL_LINKED, false)){
            determineCalendar(context, sharedPref.getString(GMAIL, null));
        }
        String authority = CalendarContract.Calendars.CONTENT_URI.getAuthority();
        for(Account account : accounts){
            if(account.name.equals(sharedPref.getString(GMAIL, null))) {
                Bundle extras = new Bundle();
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(account, authority, extras);
            }
        }
    }

    private static CalendarEvent loadEvent(Cursor csr) {
        return new CalendarEvent(csr.getString(0), new Date(csr.getLong(1)), new Date(csr.getLong(2)), !csr.getString(3).equals("0"), csr.getLong(4), csr.getString(5), csr.getInt(6), true);
    }

    private static HashSet<String> getCalenderIds(Cursor cursor) {

        HashSet<String> calendarIds = new HashSet<>();
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
    private static void determineCalendar(Context context, String accountName) {

        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
        String[] selectionArgs = new String[] {accountName};
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        if(cursor != null){
            editor.putString(GMAIL, accountName);
            editor.putBoolean(IS_EMAIL_LINKED, true);
            editor.apply();
            cursor.close();
            return;
        }

        editor.putBoolean(IS_EMAIL_LINKED, false);
        editor.apply();
    }

}