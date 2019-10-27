package com.service.istikers.alexander.istikers;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioTrack;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static com.service.istikers.alexander.istikers.NotificationKeep.TEXT_OF_NOTIFICATION;
import static com.service.istikers.alexander.istikers.NotificationKeep.TITLE_OF_NOTIFICATION;

public class AlarmClock extends AppCompatActivity {

    TextView nowTime;

    String keepName;

    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Intent intent = getIntent();
        String titleOfNotification = intent.getStringExtra(TITLE_OF_NOTIFICATION);
        String textOfNotification = intent.getStringExtra(TEXT_OF_NOTIFICATION);
        keepName = intent.getStringExtra(MainActivity.NAME_OF_KEEP);

        Intent intentService = new Intent(this, SearchNextNotify.class);
        intentService.putExtra(MainActivity.NAME_OF_KEEP, "null");
        startService(intentService);

        TextView titleKeep = (TextView) findViewById(R.id.title_keep);
        TextView textKeep = (TextView) findViewById(R.id.text_keep);

        titleKeep.setText(titleOfNotification);
        if (textOfNotification.equals("") || textOfNotification.equals(null))textKeep.setVisibility(View.GONE);
        else textKeep.setText(textOfNotification);

        nowTime = (TextView) findViewById(R.id.now_time);
        Calendar c = Calendar.getInstance();
        nowTime.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));

        TimeHandler.sendEmptyMessageDelayed(0,1000L);

        Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notify);
        AudioAttributes alarmClockAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
        r.setAudioAttributes(alarmClockAttributes);
        r.play();
    }

    @Override
    protected void onDestroy() {
        if (r != null) {
            r.stop();
            r = null;
        }
        super.onDestroy();
    }

    private Handler TimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            try {
                if (!r.isPlaying()) {
                    r.play();
                }
            } catch (Exception e) {
                TimeHandler.removeMessages(0);
            }

            Calendar c = Calendar.getInstance();

            nowTime.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", c.get(Calendar.MINUTE)));

            TimeHandler.sendEmptyMessageDelayed(0,1000L);//1 sec update
        }
    };

    public void wakeUp(View view) {
        r.stop();
        r = null;
        super.finish();
    }

    public void retimeAlarm(View view) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TYPE, 1);
        switch (view.getId()) {
            case R.id.holdower_1:
                contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME, System.currentTimeMillis() + 5 * 60 * 1000);
                break;
            case R.id.holdower_2:
                contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME, System.currentTimeMillis() + 10 * 60 * 1000);
                break;
            case R.id.holdower_3:
                contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_TIME, System.currentTimeMillis() + 15 * 60 * 1000);
                break;
        }
        contentValues.put(DBHelper.TYPE_DATA_NEXT_NOTIFY_NAME_STREAM, 0);
        dataBase.insert(keepName + SearchNextNotify.NEXT_NOTIFY_ADD_KEY, null, contentValues);

        Intent intentService = new Intent(this, SearchNextNotify.class);
        intentService.putExtra(MainActivity.NAME_OF_KEEP, "null");
        startService(intentService);

        r.stop();
        r = null;
        super.finish();
    }
}
