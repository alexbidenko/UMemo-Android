package com.service.istikers.alexander.istikers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AllUsersKeeps";
    public static final String TABLE_KEEPS = "UsersKeeps";

    public static final String KEY_ID = "_id";
    public static final String KEY_KEEP_NAME = "keep_name";
    public static final String KEY_PARAMS_KEEP_NAME = "params_keep_name";
    public static final String KEY_NEXT_EVENT_KEEP_NAME = "notification_keep_name";

    public static final String KEEP_TYPE_OF_DATA = "keepTypeOfData";
    public static final String KEEP_VALUE_OF_DATA = "keepValueOfData";
    public static final String KEEP_PARAMS_OF_DATA = "keepParamsOfData";
    public static final String TYPE_DATA_TITLE = "title";
    public static final String TYPE_DATA_PARAGRAPH = "paragraph";
    public static final String TYPE_DATA_TABLE_ITEM = "tableItem";
    public static final String TYPE_DATA_IS_NOTIFY = "isNotify";
    public static final String TYPE_DATA_DATE_EVENT = "dateEvent";
    public static final String TYPE_DATA_TIME_EVENT = "timeEvent";
    public static final String TYPE_DATA_REPEAT_EVENT = "repeatEvent";

    public static final String TYPE_DATA_NAME_NEW_NOTIFICATION = "nameNewNotification";
    public static final String TYPE_DATA_NOTIFICATION_TYPE = "notificationType";
    public static final String TYPE_DATA_NOTIFICATION_SCORE_TYPE = "notificationScoreType";
    public static final String TYPE_DATA_NOTIFICATION_BEFORE_SCORE_INDEX = "notificationBeforeScoreIndex";
    public static final String TYPE_DATA_NOTIFICATION_BEFORE_POINT = "notificationBeforePoint";
    public static final String TYPE_DATA_NOTIFICATION_TIME_EXACTLY_0 = "notificationTimeExactly_0";
    public static final String TYPE_DATA_NOTIFICATION_TIME_EXACTLY_1 = "notificationTimeExactly_1";
    public static final String TYPE_DATA_NOTIFICATION_REPEAT = "notificationRepeat";

    public static final String TYPE_DATA_NEXT_NOTIFY_NAME_STREAM = "next_notify_NAME_STREAM";
    public static final String TYPE_DATA_NEXT_NOTIFY_TYPE = "next_notify_type";
    public static final String TYPE_DATA_NEXT_NOTIFY_TIME = "next_notify_time";
    public static final String TYPE_DATA_NEXT_NOTIFY_COUNT = "next_notify_count";

    public static final String JSON_KEY_COLOR_KEEP = "color_keep";
    public static final String JSON_KEY_FONT_FAMILY_NUMBER_KEEP = "font_family_number_keep";
    public static final String JSON_KEY_FONT_FAMILY_PATH_KEEP = "font_family_path_keep";
    public static final String JSON_KEY_TEXT_SIZE_KEEP = "text_size_keep";
    public static final String JSON_KEY_DATE_REDACT = "date_redact";

    public static final String JSON_KEY_DAY = "day";
    public static final String JSON_KEY_MONTH = "month";
    public static final String JSON_KEY_YEAR = "year";
    public static final String JSON_KEY_HOUR = "hour";
    public static final String JSON_KEY_MINUTE = "minute";
    public static final String JSON_KEY_EVENT_IS_REPEATED = "is_repeated";
    public static final String JSON_KEY_EVENT_REPEATED_POINT = "event_rereated_point";
    public static final String JSON_KEY_EVENT_REPEATED_SCORE_VARIANT_NUMBER = "event_rereated_score_variant_number";
    public static final String JSON_KEY_EVENT_REPEATED_TIME_INTERVAL = "event_rereated_time_interval";
    public static final String JSON_KEY_EVENT_REPEATED_DAY_OF_WEAK = "event_rereated_day_of_weak";
    public static final String JSON_KEY_EVENT_REPEATED_END_VARIANT_NUMBER = "event_rereated_end_variant_number";
    public static final String JSON_KEY_EVENT_REPEATED_END = "event_rereated_end";

    public static final String JSON_KEY_NOTIFICATION_IS_REPEATED = "is_notification_repeated";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_POINT = "notification_rereated_point";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_SCORE_VARIANT_NUMBER = "notification_rereated_score_variant_number";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_TIME_INTERVAL = "notification_rereated_time_interval";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_DAY_OF_WEAK = "notification_rereated_day_of_weak";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_END_VARIANT_NUMBER = "notification_rereated_end_variant_number";
    public static final String JSON_KEY_NOTIFICATION_REPEATED_END = "notification_rereated_end";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_KEEPS + "(" + KEY_ID + " integer primary key, " + KEY_KEEP_NAME + " text, " +
                KEY_PARAMS_KEEP_NAME + " text, " + KEY_NEXT_EVENT_KEEP_NAME + " long)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_KEEPS);
        onCreate(db);
    }
}
