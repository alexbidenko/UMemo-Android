package com.service.istikers.alexander.istikers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SearchNextNotify extends Service {

    static String NEXT_NOTIFY_ADD_KEY = "_next_notify";

    public SearchNextNotify() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("myLog", "start service");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager1 =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel1 = new NotificationChannel("my_channel_01",
                    "Уведомления заметок",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager1.createNotificationChannel(channel1);

            NotificationManager mNotificationManager2 =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel2 = new NotificationChannel("channel_system",
                    "Работоспособность приложения",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setImportance(NotificationManager.IMPORTANCE_MIN);
            mNotificationManager2.createNotificationChannel(channel2);
        }

        String keepName = null;
        keepName = intent.getStringExtra(MainActivity.NAME_OF_KEEP);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_KEEPS, null, null, null, null, null, null);
        ContentValues contentValues = new ContentValues();

        Calendar dateEvent = Calendar.getInstance();
        boolean isStopLoop = false;
        String keepParams = null;

        if (cursor.moveToFirst() && !keepName.equals(null) && !keepName.equals("null") && !keepName.equals("")) {
            Log.d("myLog", "start save data notification");
            do {
                if (cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)).equals(keepName)) {
                    database.execSQL("drop table if exists " + keepName + NEXT_NOTIFY_ADD_KEY);
                    database.execSQL("create table " + keepName + NEXT_NOTIFY_ADD_KEY + "(_id integer primary key, " +
                            DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM + " integer, " +
                            DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE + " integer, " +
                            DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME + " long, " +
                            DBHelper.TYPE_DATA_NEXT_NOTIFY_COUNT + " integer)");

                    SQLiteDatabase dataBaseNotification = dbHelper.getWritableDatabase();
                    Cursor cursorNotification = dataBaseNotification.query(keepName + RedactKeep.NOTIFICATION_KEEP_ADD_KEY,
                            null, null, null, null, null, null);

                    boolean isStart = false;
                    int[] dateAndTimeEvent = new int[5];
                    int typeNotification = 0;
                    int scoreTypeNotification = 1;
                    int[] beforeTimeNotification = new int[4];

                    if (cursorNotification.moveToFirst()) {
                        do {
                            if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_IS_NOTIFY) &&
                                    cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("true")) isStart = true;
                            JSONObject jsonObject;
                            if (isStart) {
                                if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_DATE_EVENT)) {
                                    try {
                                        jsonObject = new JSONObject(cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                        dateAndTimeEvent[0] = jsonObject.getInt(DBHelper.JSON_KEY_YEAR);
                                        dateAndTimeEvent[1] = jsonObject.getInt(DBHelper.JSON_KEY_MONTH);
                                        dateAndTimeEvent[2] = jsonObject.getInt(DBHelper.JSON_KEY_DAY);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_TIME_EVENT)) {
                                    try {
                                        jsonObject = new JSONObject(cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                        dateAndTimeEvent[3] = jsonObject.getInt(DBHelper.JSON_KEY_HOUR);
                                        dateAndTimeEvent[4] = jsonObject.getInt(DBHelper.JSON_KEY_MINUTE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_REPEAT_EVENT)) {
                                    try {
                                        jsonObject = new JSONObject(cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                        dateEvent.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2], dateAndTimeEvent[3], dateAndTimeEvent[4]);
                                        if (dateEvent.getTimeInMillis() < System.currentTimeMillis()) {
                                            if (jsonObject.getBoolean(DBHelper.JSON_KEY_EVENT_IS_REPEATED)) {
                                                int dTime = jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_POINT);
                                                int scoreVariantNunber = jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_SCORE_VARIANT_NUMBER);
                                                int repeatCount = 0;
                                                do {
                                                    repeatCount++;
                                                    switch (scoreVariantNunber) {
                                                        case 0:
                                                            dateEvent.add(Calendar.MINUTE, dTime);
                                                            break;
                                                        case 1:
                                                            dateEvent.add(Calendar.HOUR, dTime);
                                                            break;
                                                        case 2:
                                                            dateEvent.add(Calendar.DAY_OF_YEAR, dTime);
                                                            break;
                                                        case 3:
                                                            dateEvent.add(Calendar.WEEK_OF_YEAR, dTime);
                                                            break;
                                                        case 4:
                                                            dateEvent.add(Calendar.MONTH, dTime);
                                                            break;
                                                        case 5:
                                                            dateEvent.add(Calendar.YEAR, dTime);
                                                            break;
                                                    }
                                                    switch (jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_END_VARIANT_NUMBER)) {
                                                        case 0:
                                                            if (dateEvent.getTimeInMillis() > System.currentTimeMillis()) {
                                                                dateAndTimeEvent[0] = dateEvent.get(Calendar.YEAR);
                                                                dateAndTimeEvent[1] = dateEvent.get(Calendar.MONTH);
                                                                dateAndTimeEvent[2] = dateEvent.get(Calendar.DAY_OF_MONTH);
                                                                dateAndTimeEvent[3] = dateEvent.get(Calendar.HOUR_OF_DAY);
                                                                dateAndTimeEvent[4] = dateEvent.get(Calendar.MINUTE);

                                                                isStopLoop = true;
                                                            }
                                                            break;
                                                        case 1:
                                                            Calendar endDate = Calendar.getInstance();
                                                            endDate.set(jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(2),
                                                                    jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(1),
                                                                    jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(0),
                                                                    23, 59, 59);
                                                            if (endDate.getTimeInMillis() > dateEvent.getTimeInMillis() && dateEvent.getTimeInMillis() > System.currentTimeMillis()) {
                                                                dateAndTimeEvent[0] = dateEvent.get(Calendar.YEAR);
                                                                dateAndTimeEvent[1] = dateEvent.get(Calendar.MONTH);
                                                                dateAndTimeEvent[2] = dateEvent.get(Calendar.DAY_OF_MONTH);
                                                                dateAndTimeEvent[3] = dateEvent.get(Calendar.HOUR_OF_DAY);
                                                                dateAndTimeEvent[4] = dateEvent.get(Calendar.MINUTE);
                                                                isStopLoop = true;
                                                            } else if (endDate.getTimeInMillis() < dateEvent.getTimeInMillis()) isStopLoop = true;
                                                            break;
                                                        case 2:
                                                            if (repeatCount <= jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(3) &&
                                                                    dateEvent.getTimeInMillis() > System.currentTimeMillis()) {
                                                                dateAndTimeEvent[0] = dateEvent.get(Calendar.YEAR);
                                                                dateAndTimeEvent[1] = dateEvent.get(Calendar.MONTH);
                                                                dateAndTimeEvent[2] = dateEvent.get(Calendar.DAY_OF_MONTH);
                                                                dateAndTimeEvent[3] = dateEvent.get(Calendar.HOUR_OF_DAY);
                                                                dateAndTimeEvent[4] = dateEvent.get(Calendar.MINUTE);
                                                                isStopLoop = true;
                                                            } else if (repeatCount > jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(3)) isStopLoop = true;
                                                            break;
                                                    }
                                                    Log.d("myLog", "save keepParams");
                                                    keepParams = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PARAMS_KEEP_NAME));
                                                } while (!isStopLoop);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_TYPE)) {
                                    typeNotification = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_SCORE_TYPE)) {
                                    scoreTypeNotification = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_SCORE_INDEX)) {
                                    beforeTimeNotification[0] = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_POINT)) {
                                    beforeTimeNotification[1] = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_0)) {
                                    beforeTimeNotification[2] = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_1)) {
                                    beforeTimeNotification[3] = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                } else if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NAME_NEW_NOTIFICATION)) {
                                    int notificationIndex = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    Calendar notificationTime = Calendar.getInstance();
                                    long notificationTimeInMS;
                                    if (scoreTypeNotification == 1) {
                                        switch (beforeTimeNotification[0]) {
                                            case 0:
                                                notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2], dateAndTimeEvent[3], dateAndTimeEvent[4] - beforeTimeNotification[1]);
                                                break;
                                            case 1:
                                                notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2], dateAndTimeEvent[3] - beforeTimeNotification[1], dateAndTimeEvent[4]);
                                                break;
                                            case 2:
                                                notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2] - beforeTimeNotification[1], dateAndTimeEvent[3], dateAndTimeEvent[4]);
                                                break;
                                            case 3:
                                                notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1] - beforeTimeNotification[1], dateAndTimeEvent[2], dateAndTimeEvent[3], dateAndTimeEvent[4]);
                                                break;
                                        }
                                        notificationTimeInMS = notificationTime.getTimeInMillis();
                                    } else if (scoreTypeNotification == 2) {
                                        notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2], beforeTimeNotification[2], beforeTimeNotification[3]);
                                    } else {
                                        notificationTime.set(dateAndTimeEvent[0], dateAndTimeEvent[1], dateAndTimeEvent[2], dateAndTimeEvent[3], dateAndTimeEvent[4]);
                                    }
                                    notificationTimeInMS = notificationTime.getTimeInMillis();
                                    contentValues.clear();
                                    contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE, typeNotification);
                                    contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME, notificationTimeInMS);
                                    contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM, notificationIndex);
                                    contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_COUNT, 0);
                                    database.insert(keepName + NEXT_NOTIFY_ADD_KEY, null, contentValues);
                                }
                            }
                        } while (cursorNotification.moveToNext());
                    }
                    cursorNotification.close();
                }
            } while (cursor.moveToNext());
        }
        if (!keepName.equals(null) && !keepName.equals("null") && !keepName.equals("") && isStopLoop && dateEvent.getTimeInMillis() > System.currentTimeMillis()) {
            database.delete(DBHelper.TABLE_KEEPS, DBHelper.KEY_KEEP_NAME + " =?", new String[]{keepName});
            contentValues.clear();
            contentValues.put(DBHelper.KEY_KEEP_NAME, keepName);
            contentValues.put(DBHelper.KEY_PARAMS_KEEP_NAME, keepParams);
            contentValues.put(DBHelper.KEY_NEXT_EVENT_KEEP_NAME, dateEvent.getTimeInMillis());
            database.insert(DBHelper.TABLE_KEEPS, null, contentValues);
            Log.d("myLog", "delete old row " + contentValues.getAsString(DBHelper.KEY_KEEP_NAME));
            Log.d("myLog", String.valueOf(contentValues.getAsLong(DBHelper.KEY_NEXT_EVENT_KEEP_NAME) - System.currentTimeMillis()));
        }

        cursor = database.query(DBHelper.TABLE_KEEPS, null, null, null, null, null, null);
        long nextNotificationTimeInMS = 0;
        long nextEventTimeInMS = 0;
        int nextNotificationType = 0;
        String keepNameNextEvent = null;
        if (cursor.moveToFirst()) {
            do {
                try {
                    Cursor cursorNotification = database.query(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY,
                            null, null, null, null, null, null);
                    Cursor cursorNotifyInfo = database.query(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + RedactKeep.NOTIFICATION_KEEP_ADD_KEY,
                            null, null, null, null, null, null);
                    if (cursorNotification.moveToFirst()) {
                        do {
                            int indexNotification = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM));
                            if (cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) > System.currentTimeMillis()) {
                                if (cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) < nextNotificationTimeInMS ||
                                        nextNotificationTimeInMS == 0) {
                                    keepName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME));
                                    nextNotificationTimeInMS = cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME));
                                    nextNotificationType = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE));
                                }
                            } else {
                                if (cursorNotifyInfo.moveToLast()) {
                                    do {
                                        if (indexNotification != 0) {
                                            if (cursorNotifyInfo.getString(cursorNotifyInfo.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_NAME_NEW_NOTIFICATION) &&
                                                    cursorNotifyInfo.getInt(cursorNotifyInfo.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)) == indexNotification) {
                                                cursorNotifyInfo.moveToPrevious();
                                                try {
                                                    JSONObject jsonObject = new JSONObject(cursorNotifyInfo.getString(cursorNotifyInfo.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                                    if (jsonObject.getBoolean(DBHelper.JSON_KEY_NOTIFICATION_IS_REPEATED)) {
                                                        int dTime = jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_POINT);
                                                        Calendar c = Calendar.getInstance();
                                                        c.setTimeInMillis(cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)));
                                                        switch (jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_SCORE_VARIANT_NUMBER)) {
                                                            case 0:
                                                                c.add(Calendar.MINUTE, dTime);
                                                                break;
                                                            case 1:
                                                                c.add(Calendar.HOUR, dTime);
                                                                break;
                                                            case 2:
                                                                c.add(Calendar.DAY_OF_YEAR, dTime);
                                                                break;
                                                            case 3:
                                                                c.add(Calendar.WEEK_OF_YEAR, dTime);
                                                                break;
                                                            case 4:
                                                                c.add(Calendar.MONTH, dTime);
                                                                break;
                                                            case 5:
                                                                c.add(Calendar.YEAR, dTime);
                                                                break;
                                                        }
                                                        contentValues.clear();
                                                        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE, cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE)));
                                                        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME, c.getTimeInMillis());
                                                        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM, cursorNotification.getLong(
                                                                cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM)));
                                                        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_COUNT, cursorNotification.getInt(
                                                                cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_COUNT)) + 1);

                                                        database.delete(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY,
                                                                DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME + " =?", new String[]{String.valueOf(cursorNotification.getLong(
                                                                        cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)))});

                                                        switch (jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END_VARIANT_NUMBER)) {
                                                            case 0:
                                                                database.insert(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY, null, contentValues);
                                                                break;
                                                            case 1:
                                                                Calendar endDate = Calendar.getInstance();
                                                                endDate.set(jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END).getInt(2),
                                                                        jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END).getInt(1),
                                                                        jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END).getInt(0),
                                                                        23, 59, 59);
                                                                if (endDate.getTimeInMillis() > c.getTimeInMillis())
                                                                    database.insert(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY, null, contentValues);
                                                                break;
                                                            case 2:
                                                                if (cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_COUNT)) <
                                                                        jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END).getInt(3))
                                                                    database.insert(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY, null, contentValues);
                                                                break;
                                                        }

                                                        cursorNotification.moveToFirst();

                                                        if (cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) < nextNotificationTimeInMS ||
                                                                nextNotificationTimeInMS == 0) {
                                                            keepName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME));
                                                            nextNotificationTimeInMS = cursorNotification.getLong(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME));
                                                            nextNotificationType = cursorNotification.getInt(cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE));
                                                        }
                                                    } else {
                                                        database.delete(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY,
                                                                DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME + " =?", new String[]{String.valueOf(cursorNotification.getLong(
                                                                        cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)))});
                                                    }
                                                } catch (JSONException e) {}
                                            }
                                        } else {
                                            database.delete(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY,
                                                    DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME + " =?", new String[]{String.valueOf(cursorNotification.getLong(
                                                            cursorNotification.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)))});
                                        }
                                    } while (cursorNotifyInfo.moveToPrevious());
                                }
                            }
                        } while (cursorNotification.moveToNext());
                    }
                    cursorNotification.close();
                    cursorNotifyInfo.close();
                } catch (Exception e) {}

                try {
                    Cursor cursorNextEvent = database.query(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME)) + NEXT_NOTIFY_ADD_KEY, null, null, null, null, null, null);

                    if (cursorNextEvent.moveToFirst()) {
                        do {
                            Log.d("myLog", "start " + (cursorNextEvent.getLong(cursorNextEvent.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) - System.currentTimeMillis()));
                            if (cursorNextEvent.getLong(cursorNextEvent.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) > 0) {
                                if (cursorNextEvent.getLong(cursorNextEvent.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME)) < nextEventTimeInMS ||
                                        nextEventTimeInMS == 0) {
                                    keepNameNextEvent = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME));
                                    Log.d("myLog", keepNameNextEvent);
                                    nextEventTimeInMS = cursorNextEvent.getLong(cursorNextEvent.getColumnIndex(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME));
                                }
                            }
                        } while (cursorNextEvent.moveToNext());
                    }
                } catch (Exception r) {}
            } while (cursor.moveToNext());
        }
        database.close();

        stopForeground(true);

        if (nextEventTimeInMS != 0) {
            Intent intentEvent = new Intent(this, ForegroundService.class);
            intentEvent.putExtra(MainActivity.NAME_OF_KEEP, keepNameNextEvent);

            PendingIntent pendingIntentEvent = PendingIntent.getBroadcast(this, 0, intentEvent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmForNextEvent = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmForNextEvent.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextEventTimeInMS + 20000, pendingIntentEvent);
            } else {
                alarmForNextEvent.set(AlarmManager.RTC_WAKEUP, nextEventTimeInMS + 20000, pendingIntentEvent);
            }

            Uri uri = Uri.fromFile(new File("sounds/bez_zvuka-abees.ru.mp3"));

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            //.setContentTitle(getResources().getString(R.string.app_name) + " вам напомнит!")
                            .setChannelId("channel_system")
                            .setWhen(0)
                            .setSound(uri)
                            .setPriority(Notification.PRIORITY_MIN);

            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            startForeground(1, mBuilder.build());
        }

        if (nextNotificationTimeInMS != 0) {
            SQLiteDatabase dataBaseNotification = dbHelper.getWritableDatabase();
            Cursor cursorNotification = dataBaseNotification.query(keepName + RedactKeep.TEXT_KEEP_ADD_KEY,
                    null, null, null, null, null, null);
            String titleNotification = "";
            if(cursorNotification.moveToFirst()) {
                titleNotification = cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
            }

            String textNotification = "";
            while (cursorNotification.moveToNext()) {
                if (!textNotification.equals("") && !(
                        cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("") && cursorNotification.isLast())) {
                    textNotification += "\n";
                }
                if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_TABLE_ITEM)) {
                    String text = cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                    if (text.length() > 2) {
                        if (text.substring(0, 3).equals("&t$")) {
                            textNotification += "\u2714 ";
                            textNotification += text.substring(3);
                        } else if (text.substring(0, 3).equals("&f$")) {
                            textNotification += "\u2014 ";
                            textNotification += text.substring(3);
                        } else {
                            textNotification += "\u2022 ";
                            textNotification += text;
                        }
                    } else {
                        textNotification += "\u2022 ";
                        textNotification += text;
                    }
                } else {
                    textNotification += cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                }
            }

            Intent intentNotify = new Intent(this, NotificationKeep.class);
            intentNotify.putExtra(MainActivity.NAME_OF_KEEP, keepName);
            intentNotify.putExtra(NotificationKeep.TITLE_OF_NOTIFICATION, titleNotification);
            intentNotify.putExtra(NotificationKeep.TEXT_OF_NOTIFICATION, textNotification);
            intentNotify.putExtra(NotificationKeep.TYPE_NOTIFICATION, nextNotificationType);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentNotify, PendingIntent.FLAG_CANCEL_CURRENT);

            cursorNotification.close();
            cursor.close();
            dataBaseNotification.close();

            AlarmManager alarmForNotification = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmForNotification.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextNotificationTimeInMS + 20000, pendingIntent);
            } else {
                alarmForNotification.set(AlarmManager.RTC_WAKEUP, nextNotificationTimeInMS + 20000, pendingIntent);
            }

            Uri uri = Uri.fromFile(new File("sounds/bez_zvuka-abees.ru.mp3"));

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            //.setContentTitle(getResources().getString(R.string.app_name) + " вам напомнит!")
                            .setChannelId("channel_system")
                            .setWhen(0)
                            .setSound(uri)
                            .setPriority(Notification.PRIORITY_MIN);

            Intent resultIntent = new Intent(this, MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            startForeground(1, mBuilder.build());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
