package com.example.androidproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

public class ReminderNotificationReceiver extends BroadcastReceiver {

    private String CHANNEL_ID = "SHOPPING_LIST_REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {
        DateDBHelper dateDBHelper = DateDBHelper.instanceOfDatabase(context);
        String date = dateDBHelper.getDateFromDB();
        String time = dateDBHelper.getTimeFromDB();

        boolean checkBoxFilter =  PreferenceManager.getDefaultSharedPreferences(context).getBoolean("remember", false);

        if(checkBoxFilter == true){
            Toast.makeText(context, R.string.reminder_shopping_list, Toast.LENGTH_LONG).show();

            //This is for channel create or not depend on SDK var
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = CHANNEL_ID;
                String description = CHANNEL_ID;
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getResources().getString(R.string.reminder_shopping_list))
                    .setContentText(context.getResources().getString(R.string.you_have_to_go_to_shop_at) + date + ", " + time)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
        }else{
        }

    }
}
