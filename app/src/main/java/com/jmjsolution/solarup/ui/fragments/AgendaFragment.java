package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.EventAdapter;
import com.jmjsolution.solarup.model.CalendarEvent;
import com.jmjsolution.solarup.utils.CalendarService;
import com.jmjsolution.solarup.views.EventsCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.jmjsolution.solarup.utils.CalendarService.MY_PREFS_NAME;

public class AgendaFragment extends Fragment implements EventsCalendar.Callback{

    private static final int TIME_PICKER = 13;
    @BindView(R.id.eventsCalendar) EventsCalendar eventsCalendar;
    @BindView(R.id.eventsRv) RecyclerView mEventsRv;
    @BindView(R.id.addEventFab) FloatingActionButton mAddEventBtn;
    @BindView(R.id.configureCountTv) TextView mConfigureCompteTv;
    private EventAdapter mEventAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda_fragment, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        CalendarService.syncCalendars(Objects.requireNonNull(getActivity()));

        if (sharedPref.getBoolean("isEmailConfigured", false)) {
            mConfigureCompteTv.setVisibility(View.GONE);
            CalendarService.readCalendar(getContext(), 90, 0);

            if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
                mEventAdapter = new EventAdapter(getContext(), CalendarService.mEventsList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                mEventsRv.setLayoutManager(layoutManager);
                mEventsRv.setAdapter(mEventAdapter);
            }
        } else {
            mEventsRv.setVisibility(View.GONE);
            mConfigureCompteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startSettingsFragment(4);
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

    private void startSettingsFragment(int cardviewNumber) {
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("cardviewNumber", cardviewNumber);
        settingsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, settingsFragment);
        fragmentTransaction.commit();
    }

    private void setCalendarView() {
        Calendar today = Calendar.getInstance(Locale.FRANCE);
        Calendar end = Calendar.getInstance(Locale.FRANCE);
        end.add(Calendar.YEAR, 2);
        Calendar c = Calendar.getInstance();

        eventsCalendar.setToday(today);
        eventsCalendar.setMonthRange(today, end);
        eventsCalendar.setWeekStartDay(Calendar.MONDAY, false);
        eventsCalendar.setCurrentSelectedDate(today);
        eventsCalendar.setCallback(this);
        eventsCalendar.setSelectionMode(eventsCalendar.getSINGLE_SELECTION());

        if(CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
            for (CalendarEvent e : CalendarService.mEventsList) {

                String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(e.getBegin());
                eventsCalendar.addEvent(timeStamp);
            }
        }
    }



    @Override
    public void onDaySelected(@Nullable Calendar selectedDate) {
        String timeStamp = new SimpleDateFormat("YYYY/MM/dd").format(selectedDate.getTime());
        if(timeStamp.equalsIgnoreCase(new SimpleDateFormat("YYYY/MM/dd").format(Calendar.getInstance().getTime()))
                && getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getBoolean("isEmailConfigured", false)){
            mConfigureCompteTv.setVisibility(View.GONE);
            mEventsRv.setVisibility(View.VISIBLE);
            mEventsRv.setAdapter(mEventAdapter);
        } else {
            ArrayList<CalendarEvent> eventsByDate = mEventAdapter.selectDate(timeStamp);
            if(eventsByDate.isEmpty()){
                mEventsRv.setVisibility(View.GONE);
                mConfigureCompteTv.setVisibility(View.VISIBLE);
                mConfigureCompteTv.setText("Aucun événement prévu ce jour la.");
            } else {
                mConfigureCompteTv.setVisibility(View.GONE);
                mEventsRv.setVisibility(View.VISIBLE);
                EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsByDate);
                mEventsRv.setAdapter(eventAdapter);
                mEventAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDayLongPressed(@Nullable Calendar selectedDate) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTargetFragment(AgendaFragment.this, TIME_PICKER);
        timePickerFragment.show(getFragmentManager(), "timePicker");
        }

    @Override
    public void onMonthChanged(@Nullable Calendar monthStartDate) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TIME_PICKER && resultCode == RESULT_OK){
            Bundle bund = data.getExtras();
            int hour = bund.getInt("selectedHour");
            int minute = bund.getInt("selectedMinutes");
        }
    }

    long componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (c.getTimeInMillis() / 1000L);
    }
}
