package com.jmjsolution.solarup.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.EventAdapter;
import com.jmjsolution.solarup.model.CalendarEvent;
import com.jmjsolution.solarup.services.calendarService.CalendarService;
import com.jmjsolution.solarup.views.EventsCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;

public class AgendaFragment extends Fragment implements EventsCalendar.Callback {

    private static final int TIME_PICKER = 13;
    @BindView(R.id.eventsCalendar) EventsCalendar eventsCalendar;
    @BindView(R.id.eventsRv) RecyclerView mEventsRv;
    @BindView(R.id.addEventFab) FloatingActionButton mAddEventBtn;
    @BindView(R.id.configureCountTv) TextView mConfigureCompteTv;
    private EventAdapter mEventAdapter;
    private int mHourEvent;
    private int mMinuteEvent;
    private String mTitleEvent;
    private String mLocationEvent;
    private int mDayEvent;
    private int mMonthEvent;
    private int mYearEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda_fragment, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        CalendarService.syncCalendars(Objects.requireNonNull(getActivity()));

        if (sharedPref.getBoolean(IS_EMAIL_LINKED, false)) {

            mConfigureCompteTv.setVisibility(View.GONE);
            CalendarService.readCalendar(getContext(), 90, 0);

            if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
                mEventAdapter = new EventAdapter(getContext(), CalendarService.mEventsList, getActivity());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                mEventsRv.setLayoutManager(layoutManager);
                mEventsRv.setAdapter(mEventAdapter);
            } else {
                mEventsRv.setVisibility(View.GONE);
                mConfigureCompteTv.setText(R.string.no_event_planed);
            }
        } else {
            mEventsRv.setVisibility(View.GONE);
            mConfigureCompteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startSettingsFragment();
                }
            });
        }

        mAddEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfigureCompteTv.setVisibility(View.GONE);
                mEventsRv.setVisibility(View.VISIBLE);
                mEventsRv.setAdapter(mEventAdapter);
            }
        });

        setCalendarView();


        return view;
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void onDaySelected(@Nullable Calendar selectedDate) {
        String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(Objects.requireNonNull(selectedDate).getTime());
        String tp = new SimpleDateFormat("YYYY/MM/dd").format(Calendar.getInstance().getTime());
        boolean p = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getBoolean("isEmailConfigured", false);
        if (timeStamp.equalsIgnoreCase(new SimpleDateFormat("YYYY/MM/dd").format(Calendar.getInstance().getTime()))
                && Objects.requireNonNull(getActivity()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getBoolean(IS_EMAIL_LINKED, false)) {
            mConfigureCompteTv.setVisibility(View.GONE);
            mEventsRv.setVisibility(View.VISIBLE);
            mEventsRv.setAdapter(mEventAdapter);
        } else {
                if (mEventAdapter == null) {
                    mEventsRv.setVisibility(View.GONE);
                    mConfigureCompteTv.setVisibility(View.VISIBLE);
                    mConfigureCompteTv.setText(R.string.no_event_planed);
                } else {
                    ArrayList<CalendarEvent> eventsByDate = mEventAdapter.selectDate(timeStamp);
                    if(eventsByDate.isEmpty()){
                        mEventsRv.setVisibility(View.GONE);
                        mConfigureCompteTv.setVisibility(View.VISIBLE);
                        mConfigureCompteTv.setText(R.string.no_event_planed);
                    } else {
                        mConfigureCompteTv.setVisibility(View.GONE);
                        mEventsRv.setVisibility(View.VISIBLE);
                        EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate, getActivity());
                        mEventsRv.setAdapter(eventAdapter);
                        mEventAdapter.notifyDataSetChanged();
                    }
                }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onDayLongPressed(@Nullable Calendar selectedDate) {
        mDayEvent = Integer.parseInt(new SimpleDateFormat("dd").format(Objects.requireNonNull(selectedDate).getTime()));
        mMonthEvent = Integer.parseInt(new SimpleDateFormat("MM").format(selectedDate.getTime())) - 1;
        mYearEvent = Integer.parseInt(new SimpleDateFormat("YYYY").format(selectedDate.getTime()));

        titleSetterEvent();
    }

    @Override
    public void onMonthChanged(@Nullable Calendar monthStartDate) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TIME_PICKER && resultCode == RESULT_OK) {
            Bundle bund = data.getExtras();
            mHourEvent = Objects.requireNonNull(bund).getInt("selectedHour");
            mMinuteEvent = bund.getInt("selectedMinutes");
            insertEvent();
        }
    }

    private void startSettingsFragment() {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", 4);
        settingsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, settingsFragment);
        fragmentTransaction.commit();
    }

    private void setCalendarView() {
        Calendar today = Calendar.getInstance(Locale.FRANCE);
        Calendar end = Calendar.getInstance(Locale.FRANCE);
        end.add(Calendar.YEAR, 2);

        eventsCalendar.setToday(today);
        eventsCalendar.setMonthRange(today, end);
        eventsCalendar.setWeekStartDay(Calendar.MONDAY, false);
        eventsCalendar.setCurrentSelectedDate(today);
        eventsCalendar.setCallback(this);
        eventsCalendar.setSelectionMode(eventsCalendar.getSINGLE_SELECTION());
        if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
            for (CalendarEvent e : CalendarService.mEventsList) {

                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(e.getBegin());
                eventsCalendar.addEvent(timeStamp);
            }
        }
    }

    private void titleSetterEvent(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_title_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTitleEvent = taskEditText.getText().toString();
                        locationSetterEvent();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void locationSetterEvent(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.enter_location_label)
                .setView(taskEditText)
                .setPositiveButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLocationEvent = taskEditText.getText().toString();
                        TimePickerFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.setTargetFragment(AgendaFragment.this, TIME_PICKER);
                        timePickerFragment.show(Objects.requireNonNull(getFragmentManager()), "timePicker");
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private long getGoogleCalendarId() {

        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.OWNER_ACCOUNT};

        @SuppressLint("MissingPermission")
        Cursor calCursor = Objects.requireNonNull(getContext()).getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                CalendarContract.Calendars._ID + " ASC");

        assert calCursor != null;
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);

                String email = Objects.requireNonNull(getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString("email", null));
                if (displayName.contains(email)) {
                    return id;
                }

            } while (calCursor.moveToNext());
        }
        calCursor.close();
        return -1;

    }

    private void insertEvent() {
        long startMillis;
        long endMillis;
        Calendar beginTime = Calendar.getInstance(Locale.FRANCE);
        beginTime.set(mYearEvent, mMonthEvent, mDayEvent, mHourEvent, mMinuteEvent);
        startMillis = beginTime.getTimeInMillis() ;
        Calendar endTime = Calendar.getInstance(Locale.FRANCE) ;
        endTime.set(mYearEvent, mMonthEvent, mDayEvent, mHourEvent, mMinuteEvent);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = Objects.requireNonNull(getContext()).getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, mTitleEvent);
        values.put(CalendarContract.Events.EVENT_LOCATION, mLocationEvent);
        values.put(CalendarContract.Events.CALENDAR_ID, getGoogleCalendarId());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Paris");
        @SuppressLint("MissingPermission") Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        assert uri != null;
        long eventId = Long.valueOf(uri.getLastPathSegment());
        Toast.makeText(getContext(), "Evénement ajouté.", Toast.LENGTH_SHORT).show();
        CalendarService.syncCalendars(getContext());
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ft.detach(this).attach(this).commit();
    }



}
