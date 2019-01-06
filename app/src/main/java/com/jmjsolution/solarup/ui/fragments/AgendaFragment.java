package com.jmjsolution.solarup.ui.fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.EventAdapter;
import com.jmjsolution.solarup.interfaces.DeleteEvent;
import com.jmjsolution.solarup.model.CalendarEvent;
import com.jmjsolution.solarup.services.reminderMail.ReminderMailAlarmManager;
import com.jmjsolution.solarup.services.calendarService.CalendarService;
import com.jmjsolution.solarup.views.EventsCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.Database.CALENDAR_TYPE;
import static com.jmjsolution.solarup.utils.Constants.Database.EVENTS_BRANCH;
import static com.jmjsolution.solarup.utils.Constants.Database.LOCAL_EVENTS;
import static com.jmjsolution.solarup.utils.Constants.Database.ROOT;
import static com.jmjsolution.solarup.utils.Constants.GMAIL;
import static com.jmjsolution.solarup.utils.Constants.IS_EMAIL_LINKED;

public class AgendaFragment extends Fragment implements EventsCalendar.Callback, DeleteEvent{

    private static final int TIME_PICKER = 13;
    @BindView(R.id.eventsCalendar) EventsCalendar eventsCalendar;
    @BindView(R.id.eventsRv) RecyclerView mEventsRv;
    @BindView(R.id.addEventFab) FloatingActionButton mSeeAllEventsBtn;
    @BindView(R.id.configureCountTv) TextView mConfigureCompteTv;
    @BindView(R.id.loadingEventsPb) ProgressBar mProgressBar;

    private EventAdapter mEventAdapter;
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    private int mHourEvent;
    private int mMinuteEvent;
    private String mTitleEvent;
    private String mLocationEvent;
    private int mDayEvent;
    private int mMonthEvent;
    private int mYearEvent;
    private ArrayList<CalendarEvent> mEventsListWithoutGmail;
    private boolean mIsLinked;
    private ArrayList<String> mMailAttendee = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda_fragment, container, false);
        ButterKnife.bind(this, view);

        mEventsRv.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        CalendarService.syncCalendars(Objects.requireNonNull(getActivity()));

        mEventsListWithoutGmail = new ArrayList<>();

        mIsLinked = sharedPref.getBoolean(IS_EMAIL_LINKED, false);
        setCalendarView();

        if (mIsLinked) {
            CalendarService.readCalendar(Objects.requireNonNull(getContext()), 90, 0);
            setCalendarIfGmailIsLinked();
        } else {
            setCalendarLocal();
        }

        mSeeAllEventsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfigureCompteTv.setVisibility(View.GONE);
                mEventsRv.setVisibility(View.VISIBLE);
                mEventsRv.setAdapter(mEventAdapter);
                mEventAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void setCalendarLocal() {
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                .collection(EVENTS_BRANCH).document(CALENDAR_TYPE).collection(LOCAL_EVENTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                        for (QueryDocumentSnapshot event : Objects.requireNonNull(task.getResult())) {
                            mEventsListWithoutGmail.add(event.toObject(CalendarEvent.class));
                        }

                        Collections.sort(mEventsListWithoutGmail);
                        mEventAdapter = new EventAdapter(getContext(), mEventsListWithoutGmail, getActivity(), AgendaFragment.this);
                        addEventsToCalendar();
                    }
                } else {
                    mEventsRv.setVisibility(View.GONE);
                    mConfigureCompteTv.setText(R.string.no_event_planed);
                }
            }
        });
    }

    private void setCalendarIfGmailIsLinked() {
        mConfigureCompteTv.setVisibility(View.GONE);

        if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {

            CollectionReference eventsBranchRef = mDb
                    .collection(ROOT)
                    .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                    .collection(EVENTS_BRANCH)
                    .document(CALENDAR_TYPE)
                    .collection(Objects.requireNonNull(Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null)));

            for(CalendarEvent calendarEvent : CalendarService.mEventsList){
                eventsBranchRef.document(String.valueOf(calendarEvent.getId())).set(calendarEvent);
            }

            mEventAdapter = new EventAdapter(getContext(), CalendarService.mEventsList, getActivity(), this);
            addEventsToCalendar();
        } else {
            mEventsRv.setVisibility(View.GONE);
            mConfigureCompteTv.setText(R.string.no_event_planed);
        }
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void onDaySelected(@Nullable Calendar selectedDate) {
        String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(Objects.requireNonNull(selectedDate).getTime());
        if (timeStamp.equalsIgnoreCase(new SimpleDateFormat("YYYY/MM/dd").format(Calendar.getInstance().getTime()))) {
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
                        EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate, getActivity(), this);
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

            Calendar beginTime = Calendar.getInstance(Locale.FRANCE);
            beginTime.set(mYearEvent, mMonthEvent, mDayEvent, mHourEvent, mMinuteEvent);
            Calendar endTime = Calendar.getInstance(Locale.FRANCE) ;
            endTime.set(mYearEvent, mMonthEvent, mDayEvent, mHourEvent, mMinuteEvent);

            ReminderMailAlarmManager reminderMailAlarmManager = new ReminderMailAlarmManager();
            reminderMailAlarmManager.setOneTime(getContext());

            if(!mIsLinked) {
                long currentTime = System.currentTimeMillis();
                final CalendarEvent calendarEvent = new CalendarEvent(mTitleEvent, beginTime.getTime(), endTime.getTime(), false, currentTime, mLocationEvent, 0, false);
                mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .collection(EVENTS_BRANCH).document(CALENDAR_TYPE).collection(LOCAL_EVENTS).document(String.valueOf(currentTime)).set(calendarEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mEventsListWithoutGmail.add(calendarEvent);
                            if(mEventAdapter == null){
                                mEventAdapter = new EventAdapter(getContext(), mEventsListWithoutGmail, getActivity(), AgendaFragment.this);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                mEventsRv.setLayoutManager(layoutManager);
                                mEventsRv.setAdapter(mEventAdapter);
                                mEventAdapter.notifyDataSetChanged();
                            } else {
                                Objects.requireNonNull(getFragmentManager()).beginTransaction().detach(AgendaFragment.this).attach(AgendaFragment.this).commit();
                            }
                        } else {
                            Toast.makeText(getContext(), "Echec de l'ajout de l'évenement", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                insertEvent();
            }

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
    }

    private void addEventsToCalendar() {
        if(mEventsListWithoutGmail != null && !mEventsListWithoutGmail.isEmpty()){
            for (CalendarEvent e : mEventsListWithoutGmail) {

                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(e.getBegin());
                eventsCalendar.addEvent(timeStamp);
            }
        }

        if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
            for (CalendarEvent e : CalendarService.mEventsList) {

                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(e.getBegin());
                eventsCalendar.addEvent(timeStamp);
            }
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mEventsRv.setLayoutManager(layoutManager);
        mEventsRv.setAdapter(mEventAdapter);
        mProgressBar.setVisibility(View.GONE);
        mConfigureCompteTv.setVisibility(View.GONE);
        mEventsRv.setVisibility(View.VISIBLE);
        mEventAdapter.notifyDataSetChanged();
        eventsCalendar.reset();
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
                        if(mIsLinked){
                            attendeeInvitation();
                        } else {
                            TimePickerFragment timePickerFragment = new TimePickerFragment();
                            timePickerFragment.setTargetFragment(AgendaFragment.this, TIME_PICKER);
                            timePickerFragment.show(Objects.requireNonNull(getFragmentManager()), "timePicker");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void attendeeInvitation(){
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(R.string.entrer_mail_invitation)
                .setView(taskEditText)
                .setPositiveButton(R.string.add_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMailAttendee.add(taskEditText.getText().toString());
                        attendeeInvitation();
                    }
                })
                .setNegativeButton(R.string.valid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TimePickerFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.setTargetFragment(AgendaFragment.this, TIME_PICKER);
                        timePickerFragment.show(Objects.requireNonNull(getFragmentManager()), "timePicker");
                    }
                })
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

                String email = Objects.requireNonNull(getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getString(GMAIL, null));
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

        if(mMailAttendee != null && !mMailAttendee.isEmpty()){
            for(String mail : mMailAttendee){
                CalendarService.inviteAttendees(getContext(), eventId, mail);
            }
        }

        CalendarService.syncCalendars(getContext());
        Objects.requireNonNull(getFragmentManager()).beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onDeleteEvent(final int position, long id) {
        mDb.collection(ROOT).document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                .collection(EVENTS_BRANCH).document(CALENDAR_TYPE).collection(LOCAL_EVENTS).document(String.valueOf(id)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mEventsListWithoutGmail.remove(position);
                    mEventAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
