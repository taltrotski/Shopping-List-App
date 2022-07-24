package com.example.androidproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class PhoneTurnOnReceiver extends BroadcastReceiver {

    private String CHANNEL_ID = "SHOPPING_LIST_PHONE_TURN_ON";

    @Override
    public void onReceive(Context context, Intent intent) {
        DateDBHelper dateDBHelper = DateDBHelper.instanceOfDatabase(context);
        String date = dateDBHelper.getDateFromDB();
        String time = dateDBHelper.getTimeFromDB();

        if (date.equals("") || time.equals("")) {
            //DO NOTHING
        } else {
            if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "SHOPPING_LIST_PHONE_TURN_ON";
                    String description = "SHOPPING_LIST_PHONE_TURN_ON";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("SHOPPING_LIST_PHONE_TURN_ON", name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "SHOPPING_LIST_PHONE_TURN_ON")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getResources().getString(R.string.reminder_shopping_list))
                    .setContentText(context.getResources().getString(R.string.you_have_to_go_to_shop_at) + date + ", " + time)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, builder.build());
        }
    }
}

