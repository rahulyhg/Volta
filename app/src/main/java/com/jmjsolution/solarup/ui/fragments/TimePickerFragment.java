package com.jmjsolution.solarup.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.jmjsolution.solarup.R;

import java.util.Calendar;

/**
 * Created by Samuel on 21/08/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme, this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Intent intent = new Intent();
        intent.putExtra("selectedHour", hourOfDay);
        intent.putExtra("selectedMinutes", minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
