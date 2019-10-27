package com.service.istikers.alexander.istikers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class ForegroundService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("myLog", "onStartCommand: ");
        Intent intentService = new Intent(context, SearchNextNotify.class);
        intentService.putExtra(MainActivity.NAME_OF_KEEP, intent.getStringExtra(MainActivity.NAME_OF_KEEP));
        context.startService(intentService);
    }
}
