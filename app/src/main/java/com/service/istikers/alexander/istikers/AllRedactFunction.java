package com.service.istikers.alexander.istikers;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.service.istikers.alexander.istikers.DBHelper.KEY_KEEP_NAME;
import static com.service.istikers.alexander.istikers.DBHelper.TABLE_KEEPS;
import static com.service.istikers.alexander.istikers.RedactKeep.NOTIFICATION_KEEP_ADD_KEY;
import static com.service.istikers.alexander.istikers.RedactKeep.TEXT_KEEP_ADD_KEY;
import static com.service.istikers.alexander.istikers.RedactKeep.allNotificationSwitch;
import static com.service.istikers.alexander.istikers.RedactKeep.dateDayEvent;
import static com.service.istikers.alexander.istikers.RedactKeep.dateMonthEvent;
import static com.service.istikers.alexander.istikers.RedactKeep.dateYearEvent;
import static com.service.istikers.alexander.istikers.RedactKeep.eventIsRepeated;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedDayOfWeak;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedEnd;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedEndVariantNumber;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedPoint;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedScoreVariantNumber;
import static com.service.istikers.alexander.istikers.RedactKeep.eventRereatedTimeInterval;
import static com.service.istikers.alexander.istikers.RedactKeep.fontFamilyNumber;
import static com.service.istikers.alexander.istikers.RedactKeep.fontFamilyPathKeep;
import static com.service.istikers.alexander.istikers.RedactKeep.isSaveKeepOrRemove;
import static com.service.istikers.alexander.istikers.RedactKeep.keepColorId;
import static com.service.istikers.alexander.istikers.RedactKeep.keepName;
import static com.service.istikers.alexander.istikers.RedactKeep.mainLayoutRedactKeep;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationBeforePoint;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationBeforeScoreIndex;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationBlock;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationIsRepeated;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationLocalIndex;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedDayOfWeak;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedEnd;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedEndVariantNumber;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedPoint;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedScoreVariantNumber;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationRereatedTimeInterval;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationScoreTypeIndex;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationTimeExactly;
import static com.service.istikers.alexander.istikers.RedactKeep.notificationTypeIndex;
import static com.service.istikers.alexander.istikers.RedactKeep.textSizeKeep;
import static com.service.istikers.alexander.istikers.RedactKeep.timeHourEvent;
import static com.service.istikers.alexander.istikers.RedactKeep.timeMinuteEvent;

public class AllRedactFunction extends AppCompatActivity {
    SQLiteDatabase dataBase;

    public void saveFullKeep (DBHelper dbHelper, Context con) {
        dataBase = dbHelper.getWritableDatabase();

        if (isSaveKeepOrRemove) {
            Cursor cursor = dataBase.query(TABLE_KEEPS, null, null, null, null, null, null);

            ContentValues contentValues = new ContentValues();
            if (keepName == null || keepName.equals("newKeep")) {
                keepName = "m" + System.currentTimeMillis();
            } else {
                dataBase.delete(TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{keepName});
            }
            dataBase.execSQL("drop table if exists " + keepName + TEXT_KEEP_ADD_KEY);
            dataBase.execSQL("create table " + keepName + TEXT_KEEP_ADD_KEY + "(_id integer primary key, " + DBHelper.KEEP_TYPE_OF_DATA + " text,"
                    + DBHelper.KEEP_VALUE_OF_DATA + " text," + DBHelper.KEEP_PARAMS_OF_DATA + " text)");

            for (int i = 0; i < mainLayoutRedactKeep.getChildCount(); i++) {
                if (mainLayoutRedactKeep.getChildAt(i).getId() == R.id.title_keep) {
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_TITLE);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, ((EditText) mainLayoutRedactKeep.getChildAt(i)).getText().toString());
                    dataBase.insert(keepName + TEXT_KEEP_ADD_KEY, null, contentValues);
                } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("EditText")) {
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_PARAGRAPH);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, ((EditText) mainLayoutRedactKeep.getChildAt(i)).getText().toString());
                    dataBase.insert(keepName + TEXT_KEEP_ADD_KEY, null, contentValues);
                } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("LinearLayout")) {
                    for (int j = 0; j < ((LinearLayout) ((LinearLayout) mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildCount(); j++) {
                        contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_TABLE_ITEM);
                        String text = "";
                        CheckBox cb = (CheckBox) ((RelativeLayout) ((LinearLayout) ((LinearLayout)
                                mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildAt(j)).getChildAt(0);
                        if (cb.getVisibility() == View.VISIBLE) {
                            text += (cb.isChecked()?"&t$":"&f$");
                        }
                        text += ((EditText) ((RelativeLayout) ((LinearLayout) ((LinearLayout)
                                mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildAt(j)).getChildAt(1)).getText().toString();
                        contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, text);
                        dataBase.insert(keepName + TEXT_KEEP_ADD_KEY, null, contentValues);
                    }
                }
            }

            ContentValues contentKeepNameTable = new ContentValues();
            contentKeepNameTable.put(KEY_KEEP_NAME, keepName);
            String JSONParamsKeep = "";
            JSONParamsKeep = "{'" + DBHelper.JSON_KEY_COLOR_KEEP + "':" + keepColorId + ",'" + DBHelper.JSON_KEY_FONT_FAMILY_NUMBER_KEEP + "':" + fontFamilyNumber + ",'" +
                    DBHelper.JSON_KEY_FONT_FAMILY_PATH_KEEP + "':'" + fontFamilyPathKeep + "','" + DBHelper.JSON_KEY_TEXT_SIZE_KEEP + "':" + textSizeKeep + ", '" +
                    DBHelper.JSON_KEY_DATE_REDACT + "':" + System.currentTimeMillis() + "}";

            contentKeepNameTable.put(DBHelper.KEY_PARAMS_KEEP_NAME, JSONParamsKeep);
            contentKeepNameTable.put(DBHelper.KEY_NEXT_EVENT_KEEP_NAME, 0);
            dataBase.insert(TABLE_KEEPS, null, contentKeepNameTable);

            dataBase.execSQL("drop table if exists " + keepName + NOTIFICATION_KEEP_ADD_KEY);
            dataBase.execSQL("create table " + keepName + NOTIFICATION_KEEP_ADD_KEY + "(_id integer primary key, " + DBHelper.KEEP_TYPE_OF_DATA + " text,"
                    + DBHelper.KEEP_VALUE_OF_DATA + " text)");

            contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_IS_NOTIFY);
            contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, (allNotificationSwitch.isChecked()) ? "true" : "false");
            Log.d("myLog", ((allNotificationSwitch.isChecked()) ? "true" : "false"));
            dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
            contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_DATE_EVENT);
            JSONParamsKeep = "{'"+DBHelper.JSON_KEY_DAY+"':" + dateDayEvent + ", '"+DBHelper.JSON_KEY_MONTH+"':" + dateMonthEvent + ", '"+DBHelper.JSON_KEY_YEAR+"':" + dateYearEvent + "}";
            contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
            dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
            contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_TIME_EVENT);
            JSONParamsKeep = "{'"+DBHelper.JSON_KEY_HOUR+"':" + timeHourEvent + ", '"+DBHelper.JSON_KEY_MINUTE+"':" + timeMinuteEvent + "}";
            contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
            dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);

            contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_REPEAT_EVENT);
            JSONParamsKeep = "{'"+DBHelper.JSON_KEY_EVENT_IS_REPEATED +"':" + eventIsRepeated + ", '"+DBHelper.JSON_KEY_EVENT_REPEATED_POINT+"':" + eventRereatedPoint + ", '"+
                    DBHelper.JSON_KEY_EVENT_REPEATED_SCORE_VARIANT_NUMBER+"':" + eventRereatedScoreVariantNumber +
                    ", '"+DBHelper.JSON_KEY_EVENT_REPEATED_TIME_INTERVAL+"':[" + eventRereatedTimeInterval[0] + "," + eventRereatedTimeInterval[1] + "," + eventRereatedTimeInterval[2] +
                    "," + eventRereatedTimeInterval[3] + "], '"+DBHelper.JSON_KEY_EVENT_REPEATED_DAY_OF_WEAK+"':[" + eventRereatedDayOfWeak[0] + "," + eventRereatedDayOfWeak[1] +
                    "," + eventRereatedDayOfWeak[2] + "," + eventRereatedDayOfWeak[3] + "," + eventRereatedDayOfWeak[4] + "," +
                    eventRereatedDayOfWeak[5] + "," + eventRereatedDayOfWeak[6] + "], '"+DBHelper.JSON_KEY_EVENT_REPEATED_END_VARIANT_NUMBER+"':" + eventRereatedEndVariantNumber +
                    ", '"+DBHelper.JSON_KEY_EVENT_REPEATED_END +"':[" + eventRereatedEnd[0] + "," + eventRereatedEnd[1] + "," + eventRereatedEnd[2] + "," + eventRereatedEnd[3] + "]}";
            contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
            dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);

            for (int i = 0; i < notificationBlock.length; i++) {
                if (notificationBlock[i] != null) {
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_TYPE);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationTypeIndex[i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_SCORE_TYPE);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationScoreTypeIndex[i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_SCORE_INDEX);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationBeforeScoreIndex[i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_POINT);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationBeforePoint[i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_0);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationTimeExactly[0][i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_1);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationTimeExactly[1][i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);

                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NOTIFICATION_REPEAT);
                    JSONParamsKeep = "{'"+DBHelper.JSON_KEY_NOTIFICATION_IS_REPEATED +"':" + notificationIsRepeated[i] + ", '"+DBHelper.JSON_KEY_NOTIFICATION_REPEATED_POINT+"':" + notificationRereatedPoint[i] + ", '"+
                            DBHelper.JSON_KEY_NOTIFICATION_REPEATED_SCORE_VARIANT_NUMBER+"':" + notificationRereatedScoreVariantNumber[i] +
                            ", '"+DBHelper.JSON_KEY_NOTIFICATION_REPEATED_TIME_INTERVAL+"':[" + notificationRereatedTimeInterval[i][0] + "," + notificationRereatedTimeInterval[i][1] + "," + notificationRereatedTimeInterval[i][2] +
                            "," + notificationRereatedTimeInterval[i][3] + "], '"+DBHelper.JSON_KEY_NOTIFICATION_REPEATED_DAY_OF_WEAK+"':[" + notificationRereatedDayOfWeak[i][0] + "," + notificationRereatedDayOfWeak[i][1] +
                            "," + notificationRereatedDayOfWeak[i][2] + "," + notificationRereatedDayOfWeak[i][3] + "," + notificationRereatedDayOfWeak[i][4] + "," +
                            notificationRereatedDayOfWeak[i][5] + "," + notificationRereatedDayOfWeak[i][6] + "], '"+DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END_VARIANT_NUMBER+"':" + notificationRereatedEndVariantNumber[i] +
                            ", '"+DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END +"':[" + notificationRereatedEnd[i][0] + "," + notificationRereatedEnd[i][1] + "," + notificationRereatedEnd[i][2] + "," + notificationRereatedEnd[i][3] + "]}";
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);

                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_NAME_NEW_NOTIFICATION);
                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, notificationLocalIndex[i]);
                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                }
            }

            SharedPreferences sPref = con.getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
            String login = sPref.getString("login", "");
            String pass = sPref.getString("pass", "");
            if (!login.equals("") && !pass.equals("")) {
                cursor = dataBase.query(TABLE_KEEPS, null, null, null, null, null, null);
                Map<String, Map<String, String>> mapAllKeeps = new HashMap<>();

                if (cursor.moveToFirst()) {
                    do {
                        Map<String, String> mapKeeps = new HashMap<>();
                        List<String[]> dataKeep = new ArrayList<>();
                        String nameKeepTable = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME));
                        String keepParams = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PARAMS_KEEP_NAME));
                        String keepNextEvent = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME));
                        Cursor cursorKeep = dataBase.query(nameKeepTable + TEXT_KEEP_ADD_KEY, null, null, null, null, null, null);

                        if (cursorKeep.moveToFirst()) {
                            do {
                                String[] row = {cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)),
                                        cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA))};
                                dataKeep.add(row);
                            } while (cursorKeep.moveToNext());
                        }
                        mapKeeps.put("keep_params", keepParams);
                        mapKeeps.put("keep_next_event", String.valueOf(keepNextEvent));
                        mapKeeps.put("table_keep_text", String.valueOf(new JSONArray(dataKeep)));

                        mapAllKeeps.put(nameKeepTable, mapKeeps);

                        cursorKeep.close();
                    } while (cursor.moveToNext());
                }

                SendPost send = new SendPost(con);
                send.execute("saveKeeps", login, pass, String.valueOf(new JSONObject(mapAllKeeps)));
            }

            cursor.close();
            dataBase.close();
        } else {
            if (keepName != null && !keepName.equals("newKeep")) {
                dataBase.delete(TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{keepName});
                dataBase.delete(keepName + TEXT_KEEP_ADD_KEY, null, null);
                dataBase.delete(keepName + NOTIFICATION_KEEP_ADD_KEY, null, null);
                try {
                    dataBase.delete(keepName + SearchNextNotify.NEXT_NOTIFY_ADD_KEY, null, null);
                } catch (Exception e) {}
            }

            Cursor cursor = dataBase.query(TABLE_KEEPS, null, null, null, null, null, null);
            Map<String, Map<String, String>> mapAllKeeps = new HashMap<>();

            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> mapKeeps = new HashMap<>();
                    List<String[]> dataKeep = new ArrayList<>();
                    String nameKeepTable = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KEEP_NAME));
                    String keepParams = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PARAMS_KEEP_NAME));
                    String keepNextEvent = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME));
                    Cursor cursorKeep = dataBase.query(nameKeepTable + TEXT_KEEP_ADD_KEY, null, null, null, null, null, null);

                    if (cursorKeep.moveToFirst()) {
                        do {
                            String[] row = {cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)),
                                    cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA))};
                            dataKeep.add(row);
                        } while (cursorKeep.moveToNext());
                    }
                    mapKeeps.put("keep_params", keepParams);
                    mapKeeps.put("keep_next_event", String.valueOf(keepNextEvent));
                    Log.d("sendWeb", "saveFullKeep: " + String.valueOf(new JSONArray(dataKeep)));
                    mapKeeps.put("table_keep_text", String.valueOf(new JSONArray(dataKeep)));

                    mapAllKeeps.put(nameKeepTable, mapKeeps);

                    cursorKeep.close();
                } while (cursor.moveToNext());
            }

            SharedPreferences sPref = con.getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
            String login = sPref.getString("login", "");
            String pass = sPref.getString("pass", "");
            if (!login.equals("") && !pass.equals("")) {
                SendPost send = new SendPost(con);
                send.execute("saveKeeps", login, pass, String.valueOf(new JSONObject(mapAllKeeps)));
            }

            dataBase.close();
        }
    }
}
