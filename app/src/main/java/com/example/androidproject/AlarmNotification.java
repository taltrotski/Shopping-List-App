package com.example.androidproject;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmNotification {

    private static AlarmNotification alarmNotificationInstance = null;
    private Context context;

    private  AlarmNotification(Context context){
        this.context = context;
    }

    public static AlarmNotification getAlarmInstance(Context context){
        if(alarmNotificationInstance == null){
            alarmNotificationInstance = new AlarmNotification(context);
            return alarmNotificationInstance;
        }
        return alarmNotificationInstance;
    }

    public void setNewAlarm(String tvDatePicker, String tvTimePicker){
        PendingIntent pendingIntent = null;
        Intent intent = new Intent(context, ReminderNotificationReceiver.class); // Set the broadcast class handler

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast
                    (context, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else {
            pendingIntent = PendingIntent.getActivity
                    (context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent); // need to cancel prev alarms

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = format.parse(tvDatePicker);
            String day = (String) DateFormat.format("dd", date);
            String monthNumber = (String) DateFormat.format("MM", date);
            String year = (String) DateFormat.format("yyyy", date);

            format = new SimpleDateFormat("HH:mm");
            Date time = format.parse(tvTimePicker);

            String hour = (String) DateFormat.format("HH", time);
            String min = (String) DateFormat.format("mm", time);

                    /*ListPreference listPreference = (ListPreference) findPreference("listOfHoursPreference");
                    CharSequence currText = listPreference.getEntry();
                    String currValue = listPreference.getValue();*/

            // Get SharedPreferences listOfHoursPreference value
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String currValue = sp.getString("listOfHoursPreference","-1");
            if(currValue.equals("-1")){
                currValue = "1";
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.valueOf(year));
            calendar.set(Calendar.MONTH, Integer.valueOf(monthNumber)-1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
            calendar.set(Calendar.MINUTE, Integer.valueOf(min) - Integer.valueOf(currValue));// FOR TESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT need to be in HOUR_OF_DAY
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0); 

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.e("AlarmNotification", "Complete");

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "There was problem to save the date, please try again later", Toast.LENGTH_LONG).show();
            Log.e("AlarmNotification", "Error on create ne alarm");

        }
    }
}
