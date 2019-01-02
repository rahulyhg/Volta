package com.jmjsolution.solarup.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jmjsolution.solarup.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;
import static com.jmjsolution.solarup.utils.Constants.MONTH_REMINDER;
import static com.jmjsolution.solarup.utils.Constants.THREE_DAYS_REMINDER;
import static com.jmjsolution.solarup.utils.Constants.WEEK_REMINDER;

public class ReminderMailSettingFragment extends Fragment {


    @BindView(R.id.reminderThreeDaysCb) CheckBox mThreeDaysReminderCb;
    @BindView(R.id.reminderWeekCb) CheckBox mWeekReminderCb;
    @BindView(R.id.reminderMonthCb) CheckBox mMonthReminderCb;
    SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_mail_setting_fragment, container, false);
        ButterKnife.bind(this, view);

        mSharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        boolean isThreeDayTrue = mSharedPreferences.getBoolean(THREE_DAYS_REMINDER, true);
        boolean isWeekTrue = mSharedPreferences.getBoolean(WEEK_REMINDER, true);
        boolean isMonthTrue = mSharedPreferences.getBoolean(MONTH_REMINDER, true);

        mThreeDaysReminderCb.setChecked(isThreeDayTrue);
        mWeekReminderCb.setChecked(isWeekTrue);
        mMonthReminderCb.setChecked(isMonthTrue);


        mThreeDaysReminderCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mThreeDaysReminderCb.setChecked(b);
                mSharedPreferences.edit().putBoolean(THREE_DAYS_REMINDER, b).apply();
            }
        });

        mWeekReminderCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mWeekReminderCb.setChecked(b);
                mSharedPreferences.edit().putBoolean(WEEK_REMINDER, b).apply();
            }
        });

        mMonthReminderCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mMonthReminderCb.setChecked(b);
                mSharedPreferences.edit().putBoolean(MONTH_REMINDER, b).apply();
            }
        });

        return view;
    }
}
