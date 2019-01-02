package com.jmjsolution.solarup.services.reminderMail;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.jmjsolution.solarup.services.emailService.GmailService;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.jmjsolution.solarup.services.calendarService.CalendarService.MY_PREFS_NAME;

public class ReminderMailAlarmManager extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getPackageName());
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        Bundle extras = intent.getExtras();
        StringBuilder msg = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            //Make sure this intent has been sent by the one-time timer button.
            GmailService gmailService = new GmailService(context,
                    "Hello this is sent through broadcast",
                    "jmjsolution26@gmail.com",
                    "Demande Reclamation Client",
                    null, Objects.requireNonNull(context).getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE));
            gmailService.initToken();
        }
        @SuppressLint("SimpleDateFormat") Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msg.append(formatter.format(new Date()));
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        wakeLock.release();

    }

    public void setAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderMailAlarmManager.class);
        intent.putExtra(ONE_TIME, false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
    }

    public void cancelAlarm(Context context){
        Intent intent = new Intent(context, ReminderMailAlarmManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).cancel(sender);
    }

    public void setOneTime(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderMailAlarmManager.class);
        intent.putExtra(ONE_TIME, true);
        int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        Objects.requireNonNull(alarmManager).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }


}
