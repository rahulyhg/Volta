package com.jmjsolution.solarup.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jmjsolution.solarup.R;
import com.jmjsolution.solarup.adapters.EventAdapter;
import com.jmjsolution.solarup.utils.CalendarService;
import com.jmjsolution.solarup.views.EventsCalendar;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgendaFragment extends Fragment implements EventsCalendar.Callback{

    @BindView(R.id.eventsCalendar) EventsCalendar eventsCalendar;
    @BindView(R.id.eventsRv) RecyclerView mEventsRv;
    @BindView(R.id.addEventFab) FloatingActionButton mAddEventBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.agenda_fragment, container, false);
        ButterKnife.bind(this, view);

        setCalendarView();

        CalendarService.readCalendar(getContext(), 10, 24);

        if (CalendarService.mEventsList != null && !CalendarService.mEventsList.isEmpty()) {
            EventAdapter eventAdapter = new EventAdapter(getContext(), CalendarService.mEventsList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mEventsRv.setLayoutManager(layoutManager);
            mEventsRv.setAdapter(eventAdapter);
        }

        return view;
    }

    private void setCalendarView() {
        Calendar today = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 2);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 2);


        eventsCalendar.setToday(today);
        eventsCalendar.setMonthRange(today, end);
        eventsCalendar.setWeekStartDay(Calendar.SUNDAY, false);
        eventsCalendar.setCurrentSelectedDate(today);
        eventsCalendar.addEvent(c);
        eventsCalendar.setCallback(this);
        eventsCalendar.setSelectionMode(0);
    }

    @Override
    public void onDaySelected(@Nullable Calendar selectedDate) {
        getDateString(998899);
    }

    @Override
    public void onDayLongPressed(@Nullable Calendar selectedDate) {
        getDateString(998899);
    }

    @Override
    public void onMonthChanged(@Nullable Calendar monthStartDate) {

    }

    private String getDateString(long timeInMillis) {
        if (timeInMillis != 0) {
            Calendar cal = Calendar.getInstance();
            timeInMillis = cal.getTimeInMillis();
            Toast.makeText(getActivity(), String.valueOf(timeInMillis) , Toast.LENGTH_SHORT).show();
            return String.valueOf(cal.getTimeInMillis());
        } else return "";
    }
}
