package com.service.istikers.alexander.istikers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.WindowManager;

import java.util.Random;

public class NotificationKeep extends BroadcastReceiver {
    public static String TYPE_NOTIFICATION = "com.service.istikers.alexander.istikers.typeNotification";
    public static String TITLE_OF_NOTIFICATION = "com.service.istikers.alexander.istikers.titleOfNotification";
    public static String TEXT_OF_NOTIFICATION = "com.service.istikers.alexander.istikers.textOfNotification";

    @Override
    public void onReceive(Context context, Intent intent) {
        String keepName = intent.getStringExtra(MainActivity.NAME_OF_KEEP);
        String titleOfNotification = intent.getStringExtra(TITLE_OF_NOTIFICATION);
        String textOfNotification = intent.getStringExtra(TEXT_OF_NOTIFICATION);
        int typeNotification = intent.getIntExtra(TYPE_NOTIFICATION, 0);

        if (titleOfNotification.equals("")) titleOfNotification = context.getResources().getString(R.string.app_name) + " напоминает!";

        if (typeNotification == 0) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            .setContentTitle(titleOfNotification)
                            .setContentText(textOfNotification)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(textOfNotification)
                                    .setBigContentTitle(titleOfNotification)
                                    .setSummaryText("Напоминает"))
                            .setChannelId("my_channel_01")
                            .setAutoCancel(true)
                            .setVibrate(new long[] {1500, 1000, 500})
                            .setLights(0xff4a148c, 3000, 5000)
                            .setTicker(context.getResources().getString(R.string.app_name) + " напоминает!")
                            .setWhen(System.currentTimeMillis());

            Intent resultIntent = new Intent(context, RedactKeep.class);
            resultIntent.putExtra(MainActivity.NAME_OF_KEEP, keepName);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(RedactKeep.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            Random r = new Random();
            notificationManager.notify(Integer.parseInt(keepName.substring(8)), mBuilder.build());

            Intent intentService = new Intent(context, SearchNextNotify.class);
            intentService.putExtra(MainActivity.NAME_OF_KEEP, "null");
            context.startService(intentService);
        } else if (typeNotification == 1) {
            Intent alarmClock = new Intent(context, AlarmClock.class);
            alarmClock.putExtra(MainActivity.NAME_OF_KEEP, keepName);
            alarmClock.putExtra(NotificationKeep.TITLE_OF_NOTIFICATION, titleOfNotification);
            alarmClock.putExtra(NotificationKeep.TEXT_OF_NOTIFICATION, textOfNotification);
            alarmClock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmClock);
        } else if (typeNotification == 2) {
            SharedPreferences sPref = context.getSharedPreferences("com.service.istikers.alexander.istikers", context.MODE_PRIVATE);
            String email = sPref.getString("email", "");

            SendPost send = new SendPost(context);
            send.execute("remindEmail", email, titleOfNotification, textOfNotification);
        }
    }
}
