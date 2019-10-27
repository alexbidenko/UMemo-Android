package com.service.istikers.alexander.istikers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import static com.service.istikers.alexander.istikers.AllTimeFunction.setTypeRepeatTimeTextView;
import static com.service.istikers.alexander.istikers.DBHelper.KEY_KEEP_NAME;

public class RedactKeep extends AppCompatActivity {
    AllRedactFunction allRedactFunction;

    CoordinatorLayout activityRedactKeep;
    static LinearLayout mainLayoutRedactKeep;
    ScrollView mainScrollView;
    View usedLastView;
    View usedLastTimeView;
    String SISTEM_MESSAGE = null;
    View usedLastBlock;
    static int SISTEM_NUMBER = 0;
    boolean isServiceWork = false;

    List<ArrayList<RelativeLayout>> TablesItems = new ArrayList();

    static boolean isSaveKeepOrRemove = true;

    static Switch allNotificationSwitch;
    TextView dateNotification, timeNotification, repeatEvent, paternNotification;
    LinearLayout allNotificationBlocks, addNewNotification;

    static String keepName = null;
    public static final String TEXT_KEEP_ADD_KEY = "_text_keep_add_key";
    public static final String NOTIFICATION_KEEP_ADD_KEY = "_notification_keep_add_key";
    Cursor cursorKeep;
    DBHelper dbHelper;
    SQLiteDatabase dataBase;

    int timeHour = 0;
    int timeMinute = 0;
    int dateYear = 0;
    int dateMonth = 0;
    int dateDay = 0;

    int DIALOG_TIME = 1;
    int DIALOG_DATE = 2;
    Calendar calendar;
    AlertDialog.Builder styleKeepDialogBilder, repeatTimeDialogBilder;
    AlertDialog styleKeepDialog, repeatTimeDialog;
    TextView selectedKeepColor;
    static int keepColorId;
    final String[] fontFamily = {"Sans-Serif", "Arial", "Times New Roman", "Amatic SC", "Caveat", "Comfortaa", "Comic",
            "EI Messiri", "Lobster", "Lora", "Marck Script", "Old Standart", "Roboto", "Rubik Mono One", "Segoesc",
            "Underdog"};
    static int fontFamilyNumber;
    static String fontFamilyPathKeep;
    static int textSizeKeep;

    TextView selectedDeltaTime;

    static boolean allNotificationSwitchBoolean;
    static int timeHourEvent;
    static int timeMinuteEvent;
    static int dateYearEvent, dateMonthEvent, dateDayEvent;

    static int[] arrayRereatedTimeInterval = {9, 0, 22, 0};
    static boolean[] arrayRereatedDayOfWeak = new boolean[7];
    static int[] arrayRereatedEnd = {31, 6, 2019, 10};

    LayoutInflater dialogRepeatTimeInflater;
    static View dialogRepeatTime;
    static EditText periodRepeatEventEditText;
    SeekBar typeRepeatTimeSeekBar;
    static TextView typeRepeatTimeTextView;
    static LinearLayout setRepeatTimeOnlyDays;
    static LinearLayout setRepeatTimeOnlyTimes;
    RelativeLayout typeRepeatEventNever;
    RelativeLayout typeRepeatEventDate;
    static RelativeLayout typeRepeatEventTimes;
    RadioButton typeRepeatEventNeverRadio;
    RadioButton typeRepeatEventDateRadio;
    RadioButton typeRepeatEventTimesRadio;
    static TextView typeRepeatEventDateText;
    EditText typeRepeatEventTimesNumber;

    static boolean eventIsRepeated = false;
    static int eventRereatedPoint = 10;
    static int eventRereatedScoreVariantNumber = 2;
    static int[] eventRereatedTimeInterval = {0, 0, 24, 0};
    static boolean[] eventRereatedDayOfWeak = {false, false, false, false, false, false, false};
    static int eventRereatedEndVariantNumber = 0;
    static int[] eventRereatedEnd = {31, 11, 2018, 10};
    public static int TYPE_REPEAT_EVENT = 0;

    static LinearLayout[] notificationBlock = {null, null, null, null, null, null, null, null, null, null};
    static int[] notificationLocalIndex = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    static int[] notificationTypeIndex = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] notificationScoreTypeIndex = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    static int[] notificationBeforeScoreIndex = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    static int[] notificationBeforePoint = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    static int[][] notificationTimeExactly = {{9, 9, 9, 9, 9, 9, 9, 9, 9, 9}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
    static boolean[] notificationIsRepeated = {false, false, false, false, false, false, false, false, false, false};
    static int[] notificationRereatedPoint = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
    static int[] notificationRereatedScoreVariantNumber = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    static int[][] notificationRereatedTimeInterval = {{0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0},
            {0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0}, {0, 0, 24, 0}};
    static boolean[][] notificationRereatedDayOfWeak = {{false, false, false, false, false, false, false}, {false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false}, {false, false, false, false, false, false, false}, {false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false}, {false, false, false, false, false, false, false}, {false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false}, {false, false, false, false, false, false, false}};
    static int[] notificationRereatedEndVariantNumber = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[][] notificationRereatedEnd = {{31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10},
            {31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10}, {31, 11, 2018, 10}};

    final String[] typeNotification = new String[3];
    final String[] typeScoreNotification = new String[3];
    final String[] variantDeltaTime = new String[4];

    static boolean canBottomSheetBehaviorSlide;

    SeekBar fontSize;
    TextView selectedFontFamily;
    TextView typeRepeatEventTimesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact_keep);

        MobileAds.initialize(this, "ca-app-pub-2998158216087954~9139226292");

        View navLayout = getLayoutInflater().inflate(R.layout.activity_redact_keep_ad_nav, null);
        NavigationView navigationView = findViewById(R.id.ad_nav_view);
        navigationView.addHeaderView(navLayout);

        AdView mAdView = navLayout.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("admob", "onAdLoaded: ");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("admob", "onAdFailedToLoad: ");
            }

            @Override
            public void onAdOpened() {
                Log.d("admob", "onAdOpened: ");
            }

            @Override
            public void onAdLeftApplication() {
                Log.d("admob", "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                Log.d("admob", "onAdClosed: ");
            }
        });

        mAdView.loadAd(adRequest);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 6);

        typeNotification[0] = getString(R.string.typeNotification);
        typeNotification[1] =  getString(R.string.typeAlarmClock);
        typeNotification[2] =  "email"; /*, "email", "Вибрация"*/

        typeScoreNotification[0] = getString(R.string.typeScoreExactly);
        typeScoreNotification[1] = getString(R.string.typeScoreBeforeTime);
        typeScoreNotification[2] = getString(R.string.typeScoreExactlyTime);

        variantDeltaTime[0] = getString(R.string.variantDeltaTimeMinutes);
        variantDeltaTime[1] = getString(R.string.variantDeltaTimeHours);
        variantDeltaTime[2] = getString(R.string.variantDeltaTimeDays);
        variantDeltaTime[3] = getString(R.string.variantDeltaTimeMonths);

        allNotificationSwitchBoolean = false;
        timeHourEvent = 22;
        timeMinuteEvent = 0;

        arrayRereatedTimeInterval[0] = 9;
        arrayRereatedTimeInterval[1] = 0;
        arrayRereatedTimeInterval[2] = 22;
        arrayRereatedTimeInterval[3] = 0;
        for(int i = 0; i < 7; i++)
            arrayRereatedDayOfWeak[i] = false;
        arrayRereatedEnd[0] = c.get(Calendar.DAY_OF_MONTH);
        arrayRereatedEnd[1] = c.get(Calendar.MONTH);
        arrayRereatedEnd[2] = c.get(Calendar.YEAR);
        arrayRereatedEnd[3] = 10;

        eventIsRepeated = false;

        for(int i = 0; i < notificationBlock.length; i++) {
            notificationBlock[i] = null;

            notificationTypeIndex[i] = 0;
            notificationScoreTypeIndex[i] = 1;

            notificationRereatedEnd[i][0] = c.get(Calendar.DAY_OF_MONTH);
            notificationRereatedEnd[i][1] = c.get(Calendar.MONTH);
            notificationRereatedEnd[i][2] = c.get(Calendar.YEAR);
            notificationRereatedEnd[i][3] = 10;
        }

        keepColorId = R.color.colorWhiteKeep;
        fontFamilyNumber = 0;
        fontFamilyPathKeep = "Sans-Serif";
        textSizeKeep = 20;

        final float scaleTranslate = getResources().getDisplayMetrics().density;

        isSaveKeepOrRemove = true;

        dialogRepeatTimeInflater = getLayoutInflater();
        dialogRepeatTime = dialogRepeatTimeInflater.inflate(R.layout.set_repeat_time_dialog, null);
        periodRepeatEventEditText = dialogRepeatTime.findViewById(R.id.period_repeat_event_edit_text);
        typeRepeatTimeSeekBar = dialogRepeatTime.findViewById(R.id.type_repeat_time_seek_bar);
        typeRepeatTimeTextView = dialogRepeatTime.findViewById(R.id.type_repeat_time_text_view);
        setRepeatTimeOnlyDays = dialogRepeatTime.findViewById(R.id.set_repeat_time_only_days);
        setRepeatTimeOnlyTimes = dialogRepeatTime.findViewById(R.id.set_repeat_time_only_times);
        typeRepeatEventNever = dialogRepeatTime.findViewById(R.id.type_repeat_event_never);
        typeRepeatEventDate = dialogRepeatTime.findViewById(R.id.type_repeat_event_date);
        typeRepeatEventTimes = dialogRepeatTime.findViewById(R.id.type_repeat_event_times);
        typeRepeatEventNeverRadio = dialogRepeatTime.findViewById(R.id.type_repeat_event_never_radio);
        typeRepeatEventDateRadio = dialogRepeatTime.findViewById(R.id.type_repeat_event_date_radio);
        typeRepeatEventTimesRadio = dialogRepeatTime.findViewById(R.id.type_repeat_event_times_radio);
        typeRepeatEventDateText = dialogRepeatTime.findViewById(R.id.type_repeat_event_date_text);
        typeRepeatEventTimesNumber = dialogRepeatTime.findViewById(R.id.type_repeat_event_times_number);

        dbHelper = new DBHelper(this);
        allRedactFunction = new AllRedactFunction();

        dataBase = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        keepName = intent.getStringExtra(MainActivity.NAME_OF_KEEP);

        activityRedactKeep = findViewById(R.id.activity_redact_keep);
        mainLayoutRedactKeep = findViewById(R.id.main_layout_redact_keep);
        mainScrollView = findViewById(R.id.main_scroll_view);

        calendar = Calendar.getInstance();
        dateYearEvent = calendar.get(Calendar.YEAR);
        dateMonthEvent = calendar.get(Calendar.MONTH);
        dateDayEvent = calendar.get(Calendar.DAY_OF_MONTH);

        allNotificationSwitch = findViewById(R.id.allNotificationSwitchDialog);
        dateNotification = findViewById(R.id.dateNotificationDialog);
        timeNotification = findViewById(R.id.timeNotificationDialog);
        repeatEvent = findViewById(R.id.repeat_event);
        paternNotification = findViewById(R.id.patern_notification);
        allNotificationBlocks = findViewById(R.id.all_notification_blocks);
        addNewNotification = findViewById(R.id.add_new_notification);

        LayoutInflater dialogStyleKeepInflater = getLayoutInflater();
        View dialogStyleKeep = dialogStyleKeepInflater.inflate(R.layout.set_style_dialog, null);
        styleKeepDialogBilder = new AlertDialog.Builder(this);
        styleKeepDialogBilder.setView(dialogStyleKeep);
        styleKeepDialogBilder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        styleKeepDialog = styleKeepDialogBilder.create();
        selectedKeepColor = dialogStyleKeep.findViewById(R.id.selectedKeepColor);
        selectedFontFamily = dialogStyleKeep.findViewById(R.id.font_family);

        fontSize = dialogStyleKeep.findViewById(R.id.text_size);

        repeatTimeDialogBilder = new AlertDialog.Builder(this);
        repeatTimeDialogBilder.setTitle(getString(R.string.titleRepeat));
        repeatTimeDialogBilder.setView(dialogRepeatTime);
        typeRepeatEventTimesText = dialogRepeatTime.findViewById(R.id.type_repeat_event_times_text);

        if (keepName != null && !keepName.equals("newKeep")) {
            Cursor cursorKeepParams = dataBase.query(DBHelper.TABLE_KEEPS, null,
                    null,  null, null, null, null);
            if (cursorKeepParams.moveToFirst()) {
                do {
                    if (cursorKeepParams.getString(cursorKeepParams.getColumnIndex(KEY_KEEP_NAME)).equals(keepName)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(cursorKeepParams.getString(cursorKeepParams.getColumnIndex(DBHelper.KEY_PARAMS_KEEP_NAME)));
                            keepColorId = jsonObject.getInt(DBHelper.JSON_KEY_COLOR_KEEP);

                            fontFamilyNumber = jsonObject.getInt(DBHelper.JSON_KEY_FONT_FAMILY_NUMBER_KEEP);
                            fontFamilyPathKeep = jsonObject.getString(DBHelper.JSON_KEY_FONT_FAMILY_PATH_KEEP);
                            selectedFontFamily.setText(fontFamily[fontFamilyNumber]);
                            if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                selectedFontFamily.setTypeface(Typeface.SANS_SERIF);
                            } else {
                                selectedFontFamily.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                            }

                            textSizeKeep = jsonObject.getInt(DBHelper.JSON_KEY_TEXT_SIZE_KEEP);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } while (cursorKeepParams.moveToNext());
            }
            cursorKeep = dataBase.query(keepName + TEXT_KEEP_ADD_KEY, null, null, null, null, null, null);
            if (cursorKeep.moveToFirst()) {
                String prevTypeData = null;
                do {
                    switch (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA))) {
                        case DBHelper.TYPE_DATA_TITLE:
                            prevTypeData = DBHelper.TYPE_DATA_TITLE;
                            EditText title = findViewById(R.id.title_keep);
                            title.setText(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                            if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                title.setTypeface(Typeface.SANS_SERIF);
                            } else {
                                title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                            }
                            title.setTextSize((float) (textSizeKeep * 1.5));
                            break;
                        case DBHelper.TYPE_DATA_PARAGRAPH:
                            prevTypeData = DBHelper.TYPE_DATA_PARAGRAPH;
                            EditText newParagraph = new EditText(this);
                            newParagraph.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                                    InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            newParagraph.setText(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                            if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                newParagraph.setTypeface(Typeface.SANS_SERIF);
                            } else {
                                newParagraph.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                            }
                            newParagraph.setTextSize((float) textSizeKeep);
                            newParagraph.setBackgroundResource(android.R.color.transparent);
                            mainLayoutRedactKeep.addView(newParagraph);
                            break;
                        case DBHelper.TYPE_DATA_TABLE_ITEM:
                            if (!prevTypeData.equals(DBHelper.TYPE_DATA_TABLE_ITEM)) {

                                final LinearLayout createdTable = new LinearLayout(this);
                                LinearLayout.LayoutParams createTableLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                createdTable.setOrientation(LinearLayout.VERTICAL);

                                final LinearLayout tableItems = new LinearLayout(this);
                                LinearLayout.LayoutParams tableItemsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                tableItems.setOrientation(LinearLayout.VERTICAL);
                                createdTable.addView(tableItems, tableItemsLayoutParams);

                                do {
                                    LayoutInflater itemTableInflater = getLayoutInflater();
                                    final RelativeLayout itemTable = (RelativeLayout) itemTableInflater.inflate(R.layout.activity_redact_keep_item_table, null);
                                    final EditText itemTableText = (EditText) itemTable.findViewById(R.id.item_table_text);
                                    String text = cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                        itemTableText.setTypeface(Typeface.SANS_SERIF);
                                    } else {
                                        itemTableText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                                    }
                                    itemTableText.setTextSize((float) textSizeKeep);
                                    if (text.length() > 2) {
                                        if (text.substring(0, 3).equals("&t$")) {
                                            itemTable.getChildAt(0).setVisibility(View.VISIBLE);
                                            ((CheckBox) itemTable.getChildAt(0)).setChecked(true);

                                            itemTableText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                            itemTableText.setTextColor(ContextCompat.getColor(this, R.color.colorZCheckedText));
                                            itemTableText.setText(text.substring(3));
                                        } else if (text.substring(0, 3).equals("&f$")) {
                                            itemTable.getChildAt(0).setVisibility(View.VISIBLE);

                                            itemTableText.setText(text.substring(3));
                                        } else {
                                            itemTableText.setText(text);
                                        }
                                    } else {
                                        itemTableText.setText(text);
                                    }
                                    tableItems.addView(itemTable);
                                }
                                while (cursorKeep.moveToNext() && cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals("tableItem"));

                                TextView addItemTableButton = new TextView(this);
                                addItemTableButton.setText(R.string.add_item_table);
                                addItemTableButton.setTextSize(18);
                                addItemTableButton.setPadding((int) (3 * scaleTranslate), (int) (2 * scaleTranslate), 0, 0);
                                addItemTableButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LayoutInflater itemTableInflater = getLayoutInflater();
                                        final RelativeLayout itemTable = (RelativeLayout) itemTableInflater.inflate(R.layout.activity_redact_keep_item_table, null);
                                        EditText itemTableText = itemTable.findViewById(R.id.item_table_text);
                                        if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                            itemTableText.setTypeface(Typeface.SANS_SERIF);
                                        } else {
                                            itemTableText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                                        }
                                        itemTableText.setTextSize((float) textSizeKeep);
                                        itemTable.getChildAt(0).setVisibility(((RelativeLayout) tableItems.getChildAt(0)).getChildAt(0).getVisibility());
                                        tableItems.addView(itemTable);
                                        itemTable.requestFocus();
                                    }
                                });
                                createdTable.addView(addItemTableButton);

                                mainLayoutRedactKeep.addView(createdTable, createTableLayoutParams);

                                EditText newParagraph2 = new EditText(this);
                                newParagraph2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                                        InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                if (!cursorKeep.isAfterLast()) {
                                    if (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_PARAGRAPH)) {
                                        newParagraph2.setText(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                    }
                                }
                                if (fontFamilyPathKeep.equals("Sans-Serif")) {
                                    newParagraph2.setTypeface(Typeface.SANS_SERIF);
                                } else {
                                    newParagraph2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                                }
                                newParagraph2.setTextSize((float) textSizeKeep);
                                newParagraph2.setBackgroundResource(android.R.color.transparent);
                                mainLayoutRedactKeep.addView(newParagraph2);
                            }
                            break;
                    }
                } while (cursorKeep.moveToNext());
                cursorKeepParams.close();
                cursorKeep.close();
            }
        } else {
            EditText newParagraph = new EditText(this);
            newParagraph.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            newParagraph.setBackgroundResource(android.R.color.transparent);
            newParagraph.setHint(getString(R.string.contentKeep));
            mainLayoutRedactKeep.addView(newParagraph);
        }
        selectedKeepColor.setBackgroundResource(keepColorId);
        mainScrollView.setBackgroundResource(keepColorId);

        allNotificationSwitchBooleanChange(allNotificationSwitch.isChecked());

        selectedFontFamily.setTextSize((float)textSizeKeep);
        fontSize.setProgress(textSizeKeep - 8);

        AllTimeFunction.SetTextRepeat(repeatEvent);

        allNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allNotificationSwitchBooleanChange(isChecked);
            }
        });

        TimeHandler.sendEmptyMessageDelayed(6,700L);
    }

    @SuppressLint("HandlerLeak")
    private Handler TimeHandler = new Handler() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void handleMessage(Message message) {
            final float scaleTranslate = getResources().getDisplayMetrics().density;

            ImageButton moreMenu = findViewById(R.id.menu_button);
            final ImageButton notifedMoreMenu = findViewById(R.id.notifed_menu_button);
            final PopupMenu popup = new PopupMenu(RedactKeep.this, moreMenu);
            final PopupMenu notifedPopup = new PopupMenu(RedactKeep.this, notifedMoreMenu);
            popup.getMenuInflater().inflate(R.menu.redact_keep_menu, popup.getMenu());
            notifedPopup.getMenuInflater().inflate(R.menu.redact_keep_menu, notifedPopup.getMenu());
            MenuItem shareButton = popup.getMenu().findItem(R.id.share_button);
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String sharedText = "";
                    for (int i = 0; i < mainLayoutRedactKeep.getChildCount(); i++) {
                        if (mainLayoutRedactKeep.getChildAt(i).getId() == R.id.title_keep) {
                            sharedText += ((EditText) mainLayoutRedactKeep.getChildAt(i)).getText().toString();
                            if (!sharedText.equals("")) sharedText += "\n";
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("EditText")) {
                            if (!sharedText.equals("")) sharedText += "\n";
                            sharedText += ((EditText) mainLayoutRedactKeep.getChildAt(i)).getText().toString();
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("LinearLayout")) {
                            for (int j = 0; j < ((LinearLayout) ((LinearLayout) mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildCount(); j++) {
                                if (!sharedText.equals("")) sharedText += "\n";
                                String text = ((EditText) ((RelativeLayout) ((LinearLayout) ((LinearLayout)
                                        mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildAt(j)).getChildAt(1)).getText().toString();
                                CheckBox cb = (CheckBox) ((RelativeLayout) ((LinearLayout) ((LinearLayout)
                                        mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildAt(j)).getChildAt(0);
                                if (cb.getVisibility() == View.VISIBLE) {
                                    if (cb.isChecked()) {
                                        sharedText += "\u2714 ";
                                        sharedText += text.substring(3);
                                    } else {
                                        sharedText += "\u2014 ";
                                        sharedText += text.substring(3);
                                    }
                                } else {
                                    sharedText += "\u2022 ";
                                    sharedText += text;
                                }
                            }
                        }
                    }
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.shareKeep)));
                    return false;
                }
            });

            moreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuItem make_check = popup.getMenu().findItem(R.id.make_check);
                    if (RedactKeep.this.getCurrentFocus().getId() == R.id.item_table_text) {
                        RelativeLayout focusedTextLayout = (RelativeLayout) RedactKeep.this.getCurrentFocus().getParent();
                        if (focusedTextLayout.getChildAt(0).getVisibility() == View.VISIBLE) {
                            make_check.setTitle("Отменить отмечаемость");
                        } else {
                            make_check.setTitle("Сделать отмечаемым");
                        }
                        make_check.setVisible(true);
                    } else {
                        make_check.setVisible(false);
                    }

                    popup.show();
                }
            });
            notifedMoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuItem make_check = popup.getMenu().findItem(R.id.make_check);
                    make_check.setVisible(false);
                    notifedPopup.show();
                }
            });

            final ScrollView scrollNotificationsBlock = findViewById(R.id.scroll_notifications_block);
            LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet_activity_notification);
            final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
            final TextView topBottomSheetBehavior = findViewById(R.id.top_bottom_sheet_behavior);
            // Определяет, нажал ли пользователь по шапке блока уведомлений
            // В это случае он может свернуть это окно вниз
            canBottomSheetBehaviorSlide = true;
            scrollNotificationsBlock.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN ||
                            event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (scrollNotificationsBlock.getScrollY() != 0) {
                            canBottomSheetBehaviorSlide = false;
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    } else {
                        canBottomSheetBehaviorSlide = true;
                    }
                    return false;
                }
            });

            topBottomSheetBehavior.setText(getString(R.string.redactNotifications));
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    // Запрещает поднятие окна уведомления, если не прокручено содержимое до верха
                    if (!canBottomSheetBehaviorSlide) {
                        Log.d("bottomSheetBehavior", "onStateChanged: ");
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager) RedactKeep.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(RedactKeep.this.getCurrentFocus().getWindowToken(), 0);
                        bottomSheetBehavior.setPeekHeight((int) (50 * scaleTranslate));
                    } catch (Exception ignored) {}
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        notifedMoreMenu.setVisibility(View.VISIBLE);
                        topBottomSheetBehavior.setText(getString(R.string.redactText));
                    } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        notifedMoreMenu.setVisibility(View.INVISIBLE);
                        topBottomSheetBehavior.setText(getString(R.string.redactNotifications));
                    }
                    else notifedMoreMenu.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                }
            });
            topBottomSheetBehavior.setOnClickListener(new View.OnClickListener() {
                boolean isTopBottomSheetBehaviorExpanded = false;
                @Override
                public void onClick(View v) {
                    if (!isTopBottomSheetBehaviorExpanded) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        isTopBottomSheetBehaviorExpanded = true;
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        isTopBottomSheetBehaviorExpanded = false;
                        topBottomSheetBehavior.setText(getString(R.string.redactNotifications));
                    }
                }
            });

            fontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textSizeKeep = progress + 8;
                    selectedFontFamily.setTextSize((float) textSizeKeep);
                    for (int i = 0; i < mainLayoutRedactKeep.getChildCount(); i++) {
                        if (mainLayoutRedactKeep.getChildAt(i).getId() == R.id.title_keep) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTextSize((float) (textSizeKeep * 1.5));
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("EditText")) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTextSize((float) textSizeKeep);
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("LinearLayout")) {
                            for (int j = 0; j < ((LinearLayout)((LinearLayout)mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildCount(); j++) {
                                ((EditText) ((RelativeLayout) ((LinearLayout) ((LinearLayout)
                                        mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildAt(j)).getChildAt(1)).setTextSize((float) textSizeKeep);
                            }
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            periodRepeatEventEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    int point;
                    if (periodRepeatEventEditText.getText().toString().equals("")) {
                        point = 1;
                    } else {
                        point = Integer.parseInt(periodRepeatEventEditText.getText().toString());
                    }
                    typeRepeatTimeTextView.setText(AllTimeFunction.ToRusNameOfTypeTimeRepeat(typeRepeatTimeSeekBar.getProgress(), point));
                    return false;
                }
            });

            View.OnClickListener switchTypeRepeat = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.type_repeat_event_never_radio || v.getId() == R.id.type_repeat_event_never) {
                        TYPE_REPEAT_EVENT = 0;
                        ((RadioButton)typeRepeatEventNever.getChildAt(0)).setChecked(true);
                        ((RadioButton)typeRepeatEventDate.getChildAt(0)).setChecked(false);
                        ((RadioButton)typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                    } else if (v.getId() == R.id.type_repeat_event_date_radio || v.getId() == R.id.type_repeat_event_date) {
                        TYPE_REPEAT_EVENT = 1;
                        ((RadioButton)typeRepeatEventNever.getChildAt(0)).setChecked(false);
                        ((RadioButton)typeRepeatEventDate.getChildAt(0)).setChecked(true);
                        ((RadioButton)typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                    } else if (v.getId() == R.id.type_repeat_event_times_radio || v.getId() == R.id.type_repeat_event_times ||
                            v.getId() == R.id.type_repeat_event_times_number || v.getId() == R.id.type_repeat_event_times_text) {
                        TYPE_REPEAT_EVENT = 2;
                        ((RadioButton)typeRepeatEventNever.getChildAt(0)).setChecked(false);
                        ((RadioButton)typeRepeatEventDate.getChildAt(0)).setChecked(false);
                        ((RadioButton)typeRepeatEventTimes.getChildAt(0)).setChecked(true);
                    }
                }
            };
            typeRepeatEventNever.setOnClickListener(switchTypeRepeat);
            typeRepeatEventNeverRadio.setOnClickListener(switchTypeRepeat);
            typeRepeatEventDate.setOnClickListener(switchTypeRepeat);
            typeRepeatEventDateRadio.setOnClickListener(switchTypeRepeat);
            typeRepeatEventTimes.setOnClickListener(switchTypeRepeat);
            typeRepeatEventTimesRadio.setOnClickListener(switchTypeRepeat);
            typeRepeatEventTimesNumber.setOnClickListener(switchTypeRepeat);
            typeRepeatEventTimesText.setOnClickListener(switchTypeRepeat);

            typeRepeatEventTimesNumber.setOnKeyListener(new View.OnKeyListener() {
                String repeatedEnd;
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    repeatedEnd = ((EditText) v).getText().toString();
                    if (repeatedEnd.equals("")) repeatedEnd = "0";
                    arrayRereatedEnd[3] = Integer.parseInt(repeatedEnd);
                    return false;
                }
            });

            repeatTimeDialogBilder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (periodRepeatEventEditText.getText().toString().equals("") || periodRepeatEventEditText.getText().toString().equals("0"))
                        periodRepeatEventEditText.setText("1");

                    if (usedLastView.getId() == R.id.repeat_event) {
                        eventIsRepeated = true;
                        eventRereatedPoint = Integer.parseInt(periodRepeatEventEditText.getText().toString());
                        eventRereatedScoreVariantNumber = typeRepeatTimeSeekBar.getProgress();
                        eventRereatedTimeInterval = arrayRereatedTimeInterval;
                        eventRereatedDayOfWeak = arrayRereatedDayOfWeak;
                        eventRereatedEndVariantNumber = TYPE_REPEAT_EVENT;
                        eventRereatedEnd = arrayRereatedEnd;
                    } else {
                        notificationIsRepeated[SISTEM_NUMBER] = true;
                        notificationRereatedPoint[SISTEM_NUMBER] = Integer.parseInt(periodRepeatEventEditText.getText().toString());
                        notificationRereatedScoreVariantNumber[SISTEM_NUMBER] = typeRepeatTimeSeekBar.getProgress();
                        notificationRereatedTimeInterval[SISTEM_NUMBER] = arrayRereatedTimeInterval;
                        notificationRereatedDayOfWeak[SISTEM_NUMBER] = arrayRereatedDayOfWeak;
                        notificationRereatedEndVariantNumber[SISTEM_NUMBER] = TYPE_REPEAT_EVENT;
                        notificationRereatedEnd[SISTEM_NUMBER] = arrayRereatedEnd;
                    }
                    AllTimeFunction.SetTextRepeat(usedLastView);
                }
            });
            repeatTimeDialogBilder.setNegativeButton(getString(R.string.cancelDialogButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (usedLastView.getId() == R.id.repeat_event) {
                        eventIsRepeated = false;
                        eventRereatedPoint = Integer.parseInt(periodRepeatEventEditText.getText().toString());
                        eventRereatedScoreVariantNumber = typeRepeatTimeSeekBar.getProgress();
                        eventRereatedTimeInterval = arrayRereatedTimeInterval;
                        eventRereatedDayOfWeak = arrayRereatedDayOfWeak;
                        eventRereatedEndVariantNumber = TYPE_REPEAT_EVENT;
                        eventRereatedEnd = arrayRereatedEnd;
                    } else {
                        notificationIsRepeated[SISTEM_NUMBER] = false;
                        notificationRereatedPoint[SISTEM_NUMBER] = Integer.parseInt(periodRepeatEventEditText.getText().toString());
                        notificationRereatedScoreVariantNumber[SISTEM_NUMBER] = typeRepeatTimeSeekBar.getProgress();
                        notificationRereatedTimeInterval[SISTEM_NUMBER] = arrayRereatedTimeInterval;
                        notificationRereatedDayOfWeak[SISTEM_NUMBER] = arrayRereatedDayOfWeak;
                        notificationRereatedEndVariantNumber[SISTEM_NUMBER] = TYPE_REPEAT_EVENT;
                        notificationRereatedEnd[SISTEM_NUMBER] = arrayRereatedEnd;
                    }
                    AllTimeFunction.SetTextRepeat(usedLastView);
                }
            });
            repeatTimeDialog = repeatTimeDialogBilder.create();
            typeRepeatTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setTypeRepeatTimeTextView(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            if (keepName != null && !keepName.equals("newKeep")) {
                cursorKeep = dataBase.query(keepName + NOTIFICATION_KEEP_ADD_KEY, null, null, null, null, null, null);
                if (cursorKeep.moveToFirst()) {
                    do {
                        JSONObject jsonObject;
                        try {
                            switch (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA))) {
                                case DBHelper.TYPE_DATA_IS_NOTIFY:
                                    if (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("true")) {
                                        allNotificationSwitch.setChecked(true);
                                    }
                                    Log.d("myLog", "onCreate: " + cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                    break;
                                case DBHelper.TYPE_DATA_DATE_EVENT:
                                    jsonObject = new JSONObject(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));

                                    dateYearEvent = jsonObject.getInt(DBHelper.JSON_KEY_YEAR);
                                    dateMonthEvent = jsonObject.getInt(DBHelper.JSON_KEY_MONTH);
                                    dateDayEvent = jsonObject.getInt(DBHelper.JSON_KEY_DAY);

                                    dateNotification.setText(String.format("%02d", dateDayEvent) + "." + String.format("%02d", dateMonthEvent + 1) + "." + dateYearEvent);
                                    break;
                                case DBHelper.TYPE_DATA_TIME_EVENT:
                                    jsonObject = new JSONObject(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));

                                    timeHourEvent = jsonObject.getInt(DBHelper.JSON_KEY_HOUR);
                                    timeMinuteEvent = jsonObject.getInt(DBHelper.JSON_KEY_MINUTE);

                                    timeNotification.setText(timeHourEvent + ":" + String.format("%02d", timeMinuteEvent));
                                    break;
                                case DBHelper.TYPE_DATA_REPEAT_EVENT:
                                    jsonObject = new JSONObject(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                    eventIsRepeated = jsonObject.getBoolean(DBHelper.JSON_KEY_EVENT_IS_REPEATED);
                                    eventRereatedPoint = jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_POINT);
                                    eventRereatedScoreVariantNumber = jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_SCORE_VARIANT_NUMBER);
                                    for (int i = 0; i < 4; i++) {
                                        eventRereatedTimeInterval[i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_TIME_INTERVAL).getInt(i);
                                    }
                                    for (int i = 0; i < 7; i++) {
                                        eventRereatedDayOfWeak[i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_DAY_OF_WEAK).getBoolean(i);
                                    }
                                    eventRereatedEndVariantNumber = jsonObject.getInt(DBHelper.JSON_KEY_EVENT_REPEATED_END_VARIANT_NUMBER);
                                    for (int i = 0; i < 4; i++) {
                                        eventRereatedEnd[i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_EVENT_REPEATED_END).getInt(i);
                                    }
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_TYPE:
                                    notificationTypeIndex[allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_SCORE_TYPE:
                                    notificationScoreTypeIndex[allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_SCORE_INDEX:
                                    notificationBeforeScoreIndex[allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_BEFORE_POINT:
                                    notificationBeforePoint[allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_0:
                                    notificationTimeExactly[0][allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_TIME_EXACTLY_1:
                                    notificationTimeExactly[1][allNotificationBlocks.getChildCount()] = cursorKeep.getInt(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    break;
                                case DBHelper.TYPE_DATA_NOTIFICATION_REPEAT:
                                    jsonObject = new JSONObject(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                    notificationIsRepeated[allNotificationBlocks.getChildCount()] = jsonObject.getBoolean(DBHelper.JSON_KEY_NOTIFICATION_IS_REPEATED);
                                    notificationRereatedPoint[allNotificationBlocks.getChildCount()] = jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_POINT);
                                    notificationRereatedScoreVariantNumber[allNotificationBlocks.getChildCount()] = jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_SCORE_VARIANT_NUMBER);
                                    for (int i = 0; i < 4; i++) {
                                        notificationRereatedTimeInterval[allNotificationBlocks.getChildCount()][i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_TIME_INTERVAL).getInt(i);
                                    }
                                    for (int i = 0; i < 7; i++) {
                                        notificationRereatedDayOfWeak[allNotificationBlocks.getChildCount()][i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_DAY_OF_WEAK).getBoolean(i);
                                    }
                                    notificationRereatedEndVariantNumber[allNotificationBlocks.getChildCount()] = jsonObject.getInt(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END_VARIANT_NUMBER);
                                    for (int i = 0; i < 4; i++) {
                                        notificationRereatedEnd[allNotificationBlocks.getChildCount()][i] = jsonObject.getJSONArray(DBHelper.JSON_KEY_NOTIFICATION_REPEATED_END).getInt(i);
                                    }
                                    break;
                                case DBHelper.TYPE_DATA_NAME_NEW_NOTIFICATION:
                                    addNewNotificationBlock();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } while (cursorKeep.moveToNext());
                    cursorKeep.close();
                }
            } else {
                dateNotification.setText(String.format("%02d", dateDayEvent) + "." + String.format("%02d", dateMonthEvent + 1) + "." + dateYearEvent);
                timeNotification.setText(timeHourEvent + ":" + String.format("%02d", timeMinuteEvent));
            }

            dataBase.close();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        allRedactFunction.saveFullKeep(dbHelper, this);

        startService();
    }

    public void saveKeep(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                RedactKeep.this
        );
        ActivityCompat.startActivity(RedactKeep.this, intent, options.toBundle());
    }

    void startService () {
        if (!isServiceWork) {
            Intent intentEvent = new Intent(this, ForegroundService.class);
            PendingIntent pendingIntentEvent = PendingIntent.getBroadcast(this, 0, intentEvent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmForNextEvent = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmForNextEvent.cancel(pendingIntentEvent);

            Intent intentNotify = new Intent(this, NotificationKeep.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentNotify, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmForNotification = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmForNotification.cancel(pendingIntent);

            Intent intentService = new Intent(this, SearchNextNotify.class);
            intentService.putExtra(MainActivity.NAME_OF_KEEP, keepName);
            startService(intentService);
            isServiceWork = true;
        }
    }

    public void deleteKeep(MenuItem item) {
        isSaveKeepOrRemove = false;

        Intent intent = new Intent(this, MainActivity.class);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                RedactKeep.this
        );
        ActivityCompat.startActivity(RedactKeep.this, intent, options.toBundle());
    }

    public void createTable(MenuItem item) {
        createTableFunction();
    }

    public void SetTimeDialog(View view) {
        usedLastTimeView = view;
        timeHour = timeHourEvent;
        timeMinute = timeMinuteEvent;
        showDialog(DIALOG_TIME);
    }

    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_TIME) {
            return new TimePickerDialog(this, TimePickerSetTime, timeHour, timeMinute, true);
        } else if (id == DIALOG_DATE) {
            return new DatePickerDialog(this, DatePickerSetDate, dateYear, dateMonth, dateDay);
        }
        return super.onCreateDialog(id);
    }

    public void SetDateDialog(View view) {
        dateYear = dateYearEvent;
        dateMonth = dateMonthEvent;
        dateDay = dateDayEvent;
        usedLastTimeView = view;
        showDialog(DIALOG_DATE);
    }

    TimePickerDialog.OnTimeSetListener TimePickerSetTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (usedLastTimeView.getId() == R.id.timeNotificationDialog) {
                timeHourEvent = hourOfDay;
                timeMinuteEvent = minute;
                timeNotification.setText(timeHourEvent + ":" + String.format("%02d", timeMinuteEvent));
            } else if (usedLastTimeView.getId() == R.id.only_times_start_interval) {
                arrayRereatedTimeInterval[0] = hourOfDay;
                arrayRereatedTimeInterval[1] = minute;
                ((TextView)usedLastTimeView).setText(hourOfDay + ":" + String.format("%02d", minute));
            } else if (usedLastTimeView.getId() == R.id.only_times_end_interval) {
                arrayRereatedTimeInterval[2] = hourOfDay;
                arrayRereatedTimeInterval[3] = minute;
                ((TextView)usedLastTimeView).setText(hourOfDay + ":" + String.format("%02d", minute));
            } else if (SISTEM_MESSAGE.equals("set_delta_time_notification")) {
                notificationTimeExactly[0][allNotificationBlocks.indexOfChild(usedLastView)] = hourOfDay;
                notificationTimeExactly[1][allNotificationBlocks.indexOfChild(usedLastView)] = minute;
                ((TextView)usedLastTimeView).setText(getString(R.string.inWord) + " " + hourOfDay + ":" +
                        String.format("%02d", minute));
            }
        }
    };

    DatePickerDialog.OnDateSetListener DatePickerSetDate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (usedLastTimeView.getId() == R.id.type_repeat_event_date_text) {
                arrayRereatedEnd[0] = dayOfMonth;
                arrayRereatedEnd[1] = monthOfYear;
                arrayRereatedEnd[2] = year;
                ((TextView) dialogRepeatTime.findViewById(R.id.type_repeat_event_date_text)).setText(String.format("%02d", dayOfMonth) + "." +
                        String.format("%02d", monthOfYear + 1) + "." + year);
            } else {
                dateYearEvent = year;
                dateMonthEvent = monthOfYear;
                dateDayEvent = dayOfMonth;
                dateNotification.setText(String.format("%02d", dateDayEvent) + "." + String.format("%02d", dateMonthEvent + 1) + "." + dateYearEvent);
            }
        }
    };

    public void addStyleKeepDialog(MenuItem item) {
        styleKeepDialog.show();
    }

    public void selectColorKeep(View view) {
        switch (view.getId()) {
            case R.id.colorWhiteKeep:
                keepColorId = R.color.colorWhiteKeep;
                break;
            case R.id.colorRedKeep:
                keepColorId = R.color.colorRedKeep;
                break;
            case R.id.colorOrangeKeep:
                keepColorId = R.color.colorOrangeKeep;
                break;
            case R.id.colorYellowKeep:
                keepColorId = R.color.colorYellowKeep;
                break;
            case R.id.colorGreenKeep:
                keepColorId = R.color.colorGreenKeep;
                break;
            case R.id.colorLightBlueKeep:
                keepColorId = R.color.colorLightBlueKeep;
                break;
            case R.id.colorBlueKeep:
                keepColorId = R.color.colorBlueKeep;
                break;
            case R.id.colorPurpurKeep:
                keepColorId = R.color.colorPurpurKeep;
                break;
        }
        selectedKeepColor.setBackgroundResource(keepColorId);
        mainScrollView.setBackgroundResource(keepColorId);
    }

    public void addNotificationButton(View view) {
        for (LinearLayout aNotificationBlock : notificationBlock) {
            if (aNotificationBlock == null) {
                addNewNotificationBlock();
                return;
            }
        }
    }

    public void setFontFamily(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RedactKeep.this);
        builder.setTitle(getString(R.string.chooseFontFamily));

        final TextView viewFontFamily = (TextView) view;

        for (int i = 0; i < fontFamily.length; i++) {
            if (viewFontFamily.getText().equals(fontFamily[i])) {
                fontFamilyNumber = i;
            }
        }
        builder.setSingleChoiceItems(fontFamily, fontFamilyNumber, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fontFamilyNumber = which;
                switch (which) {
                    case 0:
                        fontFamilyPathKeep = "Sans-Serif";
                        break;
                    case 1:
                        fontFamilyPathKeep = "arial.ttf";
                        break;
                    case 2:
                        fontFamilyPathKeep = "times.ttf";
                        break;
                    case 3:
                        fontFamilyPathKeep = "amaticsc.ttf";
                        break;
                    case 4:
                        fontFamilyPathKeep = "caveat.ttf";
                        break;
                    case 5:
                        fontFamilyPathKeep = "comfortaa.ttf";
                        break;
                    case 6:
                        fontFamilyPathKeep = "comic.ttf";
                        break;
                    case 7:
                        fontFamilyPathKeep = "eimessiri.ttf";
                        break;
                    case 8:
                        fontFamilyPathKeep = "lobster.ttf";
                        break;
                    case 9:
                        fontFamilyPathKeep = "lora.ttf";
                        break;
                    case 10:
                        fontFamilyPathKeep = "marckscript.ttf";
                        break;
                    case 11:
                        fontFamilyPathKeep = "oldstandard.ttf";
                        break;
                    case 12:
                        fontFamilyPathKeep = "roboto.ttf";
                        break;
                    case 13:
                        fontFamilyPathKeep = "rubikmonoone.ttf";
                        break;
                    case 14:
                        fontFamilyPathKeep = "segoesc.ttf";
                        break;
                    case 15:
                        fontFamilyPathKeep = "underdog.ttf";
                        break;
                }
            }
        });
        builder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewFontFamily.setText(fontFamily[fontFamilyNumber]);
                if (fontFamilyPathKeep.equals("Sans-Serif")) {
                    viewFontFamily.setTypeface(Typeface.SANS_SERIF);
                    for (int i = 0; i < mainLayoutRedactKeep.getChildCount(); i++) {
                        if (mainLayoutRedactKeep.getChildAt(i).getId() == R.id.title_keep) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTypeface(Typeface.SANS_SERIF);
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("EditText")) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTypeface(Typeface.SANS_SERIF);
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("LinearLayout")) {
                            for (int j = 0; j < ((LinearLayout)((LinearLayout)mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildCount(); j++) {
                                ((EditText)((RelativeLayout)((LinearLayout)((LinearLayout)mainLayoutRedactKeep.getChildAt(i))
                                        .getChildAt(0)).getChildAt(j)).getChildAt(1)).setTypeface(Typeface.SANS_SERIF);
                            }
                        }
                    }
                } else {
                    viewFontFamily.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                    for (int i = 0; i < mainLayoutRedactKeep.getChildCount(); i++) {
                        if (mainLayoutRedactKeep.getChildAt(i).getId() == R.id.title_keep) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("EditText")) {
                            ((EditText)mainLayoutRedactKeep.getChildAt(i)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                        } else if (mainLayoutRedactKeep.getChildAt(i).getClass().getSimpleName().equals("LinearLayout")) {
                            for (int j = 0; j < ((LinearLayout)((LinearLayout)mainLayoutRedactKeep.getChildAt(i)).getChildAt(0)).getChildCount(); j++) {
                                ((EditText) ((RelativeLayout) ((LinearLayout) ((LinearLayout) mainLayoutRedactKeep.getChildAt(i)).
                                        getChildAt(0)).getChildAt(j)).getChildAt(1)).
                                        setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + fontFamilyPathKeep));
                            }
                        }
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setRepeatDialog(View view) {
        usedLastView = view;
        if (view.getId() == R.id.repeat_event) {
            periodRepeatEventEditText.setText(String.valueOf(eventRereatedPoint));
            typeRepeatTimeSeekBar.setProgress(eventRereatedScoreVariantNumber);
            setTypeRepeatTimeTextView(eventRereatedScoreVariantNumber);
            for (int i = 0; i < 7; i++) {
                if (eventRereatedDayOfWeak[i]) {
                    ((LinearLayout) dialogRepeatTime.findViewById(R.id.set_repeat_time_only_days_linear_layout)).getChildAt(i).setBackgroundResource(R.color.colorLightBlueKeep);
                } else {
                    ((LinearLayout) dialogRepeatTime.findViewById(R.id.set_repeat_time_only_days_linear_layout)).getChildAt(i).setBackgroundResource(R.color.colorWhiteKeep);
                }
            }
            ((TextView) dialogRepeatTime.findViewById(R.id.only_times_start_interval)).setText(eventRereatedTimeInterval[0] + ":" + String.format("%02d", eventRereatedTimeInterval[1]));
            ((TextView) dialogRepeatTime.findViewById(R.id.only_times_end_interval)).setText(eventRereatedTimeInterval[2] + ":" + String.format("%02d", eventRereatedTimeInterval[3]));
            switch (eventRereatedEndVariantNumber) {
                case 0:
                    TYPE_REPEAT_EVENT = 0;
                    ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(true);
                    ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(false);
                    ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                    break;
                case 1:
                    TYPE_REPEAT_EVENT = 1;
                    ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(false);
                    ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(true);
                    ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                    break;
                case 2:
                    TYPE_REPEAT_EVENT = 2;
                    ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(false);
                    ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(false);
                    ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(true);
                    break;
            }
            typeRepeatEventDateText.setText(String.format("%02d", eventRereatedEnd[0]) + "." +
                    String.format("%02d", eventRereatedEnd[1] + 1) + "." + eventRereatedEnd[2]);
            typeRepeatEventTimesNumber.setText(String.valueOf(eventRereatedEnd[3]));
        }

        repeatTimeDialog.show();
    }

    public void editRereatedDayOfWeak(View view) {
        if (!arrayRereatedDayOfWeak[((LinearLayout) view.getParent()).indexOfChild(view)]) {
            arrayRereatedDayOfWeak[((LinearLayout) view.getParent()).indexOfChild(view)] = true;
            view.setBackgroundResource(R.color.colorLightBlueKeep);
        } else {
            view.setBackgroundResource(R.color.colorWhiteKeep);
            arrayRereatedDayOfWeak[((LinearLayout) view.getParent()).indexOfChild(view)] = false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 67/*BackSpace*/) {
            removeEditTextAreaFun();
        }
        return super.onKeyUp(keyCode, event);
    }

    void removeEditTextAreaFun() {
        try {
            if (RedactKeep.this.getCurrentFocus().getId() == R.id.item_table_text) {
                RelativeLayout focusedTextLayout = (RelativeLayout) RedactKeep.this.getCurrentFocus().getParent();
                if(((LinearLayout) focusedTextLayout.getParent()).getChildCount() == 1) {
                    mainLayoutRedactKeep.removeView((LinearLayout)focusedTextLayout.getParent().getParent());
                } else {
                    int index = ((LinearLayout) focusedTextLayout.getParent()).indexOfChild(focusedTextLayout);
                    ((LinearLayout) focusedTextLayout.getParent()).removeView(focusedTextLayout);
                    if (index == 0) {
                        ((LinearLayout) focusedTextLayout.getParent()).getChildAt(0).requestFocus();
                    } else {
                        ((LinearLayout) focusedTextLayout.getParent()).getChildAt(index - 1).requestFocus();
                    }
                }
            } else if (((LinearLayout)RedactKeep.this.getCurrentFocus().getParent()).indexOfChild(
                        RedactKeep.this.getCurrentFocus()
                    ) > 2)
            {
                ((LinearLayout)RedactKeep.this.getCurrentFocus().getParent()).removeView(
                        RedactKeep.this.getCurrentFocus()
                );
            }
        } catch (Exception e) {}
    }

    void allNotificationSwitchBooleanChange (boolean b) {
        allNotificationSwitchBoolean = b;

        dateNotification.setEnabled(b);
        timeNotification.setEnabled(b);
        repeatEvent.setEnabled(b);
        paternNotification.setEnabled(b);
        if (b) {
            allNotificationBlocks.setVisibility(View.VISIBLE);
            addNewNotification.setVisibility(View.VISIBLE);
        } else {
            allNotificationBlocks.setVisibility(View.GONE);
            addNewNotification.setVisibility(View.GONE);
        }
    }

    public void setOnlyTimesInterval(View view) {
        if (usedLastView.getId() == R.id.repeat_event) {
            if (view.getId() == R.id.only_times_start_interval) {
                timeHour = eventRereatedTimeInterval[0];
                timeMinute = eventRereatedTimeInterval[1];
            } else if (view.getId() == R.id.only_times_end_interval) {
                timeHour = eventRereatedTimeInterval[2];
                timeMinute = eventRereatedTimeInterval[3];
            }
        } else {
            if (view.getId() == R.id.only_times_start_interval) {
                timeHour = notificationRereatedTimeInterval[SISTEM_NUMBER][0];
                timeMinute = notificationRereatedTimeInterval[SISTEM_NUMBER][1];
            } else if (view.getId() == R.id.only_times_end_interval) {
                timeHour = notificationRereatedTimeInterval[SISTEM_NUMBER][2];
                timeMinute = notificationRereatedTimeInterval[SISTEM_NUMBER][3];
            }
        }
        usedLastTimeView = view;
        showDialog(DIALOG_TIME);
    }

    public void setEndOfRepeatDate(View view) {
        TYPE_REPEAT_EVENT = 1;
        ((RadioButton)typeRepeatEventNever.getChildAt(0)).setChecked(false);
        ((RadioButton)typeRepeatEventDate.getChildAt(0)).setChecked(true);
        ((RadioButton)typeRepeatEventTimes.getChildAt(0)).setChecked(false);
        if (usedLastView.getId() == R.id.repeat_event) {
            dateYear = eventRereatedEnd[2];
            dateMonth = eventRereatedEnd[1];
            dateDay = eventRereatedEnd[0];
        } else {
            dateYear = notificationRereatedEnd[SISTEM_NUMBER][2];
            dateMonth = notificationRereatedEnd[SISTEM_NUMBER][1];
            dateDay = notificationRereatedEnd[SISTEM_NUMBER][0];
        }
        usedLastTimeView = view;
        showDialog(DIALOG_DATE);
    }

    public View[] removeNotificationBlockAndData (View[] arrayView, int removedViewIndex) {
        arrayView[removedViewIndex] = null;
        for (int i = removedViewIndex; i < arrayView.length; i++) {
            if (i + 1 != arrayView.length) {
                arrayView[i] = arrayView[i + 1];
                arrayView[i + 1] = null;
                notificationTypeIndex[i] = notificationTypeIndex[i + 1];
                notificationTypeIndex[i + 1] = 0;
                notificationScoreTypeIndex[i] = notificationScoreTypeIndex[i + 1];
                notificationScoreTypeIndex[i + 1] = 1;
                notificationBeforePoint[i] = notificationBeforePoint[i + 1];
                notificationBeforePoint[i + 1] = 1;
                notificationTimeExactly[0][i] = notificationTimeExactly[0][i + 1];
                notificationTimeExactly[0][i + 1] = 9;
                notificationTimeExactly[1][i] = notificationTimeExactly[1][i + 1];
                notificationTimeExactly[1][i + 1] = 0;
                notificationIsRepeated[i] = notificationIsRepeated[i + 1];
                notificationIsRepeated[i + 1] = false;
                notificationRereatedPoint[i] = notificationRereatedPoint[i + 1];
                notificationRereatedPoint[i + 1] = 10;
                notificationRereatedScoreVariantNumber[i] = notificationRereatedScoreVariantNumber[i + 1];
                notificationRereatedScoreVariantNumber[i + 1] = 2;
                notificationRereatedTimeInterval[i] = notificationRereatedTimeInterval[i + 1];
                notificationRereatedTimeInterval[i + 1] = new int[] {9, 0, 22, 0};
                notificationRereatedDayOfWeak[i] = notificationRereatedDayOfWeak[i + 1];
                notificationRereatedDayOfWeak[i + 1] = new boolean[] {false, false, false, false, false, false, false};
                notificationRereatedEndVariantNumber[i] = notificationRereatedEndVariantNumber[i + 1];
                notificationRereatedEndVariantNumber[i + 1] = 0;
                notificationRereatedEnd[i] = notificationRereatedEnd[i + 1];
                notificationRereatedEnd[i + 1] = new int[] {31, 11, 2018, 10};
                notificationLocalIndex[i] = notificationLocalIndex[i + 1];
                notificationLocalIndex[i + 1] = notificationLocalIndex[i];
            }
        }
        return arrayView;
    }

    public void addNewNotificationBlock () {
        LayoutInflater notificationBlockInflater = getLayoutInflater();
        final LinearLayout block = (LinearLayout) notificationBlockInflater.inflate(R.layout.activity_notification_notification_block, null);

        final float scaleTranslate = getResources().getDisplayMetrics().density;
        Button typeN = block.findViewById(R.id.type_notification);
        final ImageView iconN = block.findViewById(R.id.icon_notification);
        typeN.setText(typeNotification[notificationTypeIndex[allNotificationBlocks.getChildCount()]]);
        if (notificationTypeIndex[allNotificationBlocks.getChildCount()] == 0) iconN.setImageResource(R.drawable.ic_notifications_black_24dp);
        else if (notificationTypeIndex[allNotificationBlocks.getChildCount()] == 1) iconN.setImageResource(R.drawable.ic_alarm_black_24dp);
        else if (notificationTypeIndex[allNotificationBlocks.getChildCount()] == 2) iconN.setImageResource(R.drawable.ic_email_primary_24dp);
        View.OnClickListener selectTypeNotification = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RedactKeep.this);
                builder.setTitle(getString(R.string.typeNotificationTitleDialog));

                final TextView viewNotification = (TextView) v;

                int checkedItem = 0;

                final String[] choiced = {null};

                for (int i = 0; i < typeNotification.length; i++) {
                    if (viewNotification.getText().equals(typeNotification[i])) {
                        checkedItem = i;
                        choiced[0] = typeNotification[i];
                    }
                }
                final int[] checked = new int[1];
                builder.setSingleChoiceItems(typeNotification, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiced[0] = typeNotification[which];
                        checked[0] = which;
                    }
                });

                builder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewNotification.setText(choiced[0]);
                        notificationTypeIndex[allNotificationBlocks.indexOfChild(block)] = checked[0];
                        if (notificationTypeIndex[allNotificationBlocks.indexOfChild(block)] == 0) iconN.setImageResource(R.drawable.ic_notifications_black_24dp);
                        else if (notificationTypeIndex[allNotificationBlocks.indexOfChild(block)] == 1) iconN.setImageResource(R.drawable.ic_alarm_black_24dp);
                        else if (notificationTypeIndex[allNotificationBlocks.indexOfChild(block)] == 2) {
                            iconN.setImageResource(R.drawable.ic_email_primary_24dp);

                            SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
                            String email = sPref.getString("email", "");

                            if(email.equals("")) {
                                AlertDialog.Builder emailBuilder = new AlertDialog.Builder(RedactKeep.this);
                                final EditText enterEmail = new EditText(RedactKeep.this);
                                emailBuilder.setTitle("Введите email");
                                enterEmail.setHint("email");
                                emailBuilder.setView(enterEmail);
                                emailBuilder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sPref.edit();
                                        editor.putString("email", enterEmail.getText().toString());
                                        editor.apply();
                                    }
                                });

                                AlertDialog emailDialog = emailBuilder.create();
                                emailDialog.show();
                            }
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        typeN.setOnClickListener(selectTypeNotification);
        ImageButton cancelN = block.findViewById(R.id.cancel_notification);
        View.OnClickListener cancelNotification = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout block = (LinearLayout)v.getParent().getParent();
                notificationBlock = (LinearLayout[]) removeNotificationBlockAndData(notificationBlock, ((LinearLayout)block.getParent()).indexOfChild(block));
                ((LinearLayout)block.getParent()).removeView(block);
            }
        };
        cancelN.setOnClickListener(cancelNotification);

        Button durationN = block.findViewById(R.id.type_score_notification);
        final Button timeN = block.findViewById(R.id.notification_before);
        durationN.setText(typeScoreNotification[notificationScoreTypeIndex[allNotificationBlocks.getChildCount()]]);
        View.OnClickListener selectIndicationNotification = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RedactKeep.this);
                builder.setTitle(getString(R.string.notificationScoreTypeTitleDialog));

                final TextView viewNotification = (TextView) v;

                int checkedItem = notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)];

                final String[] choiced = {null};

                for (int i = 0; i < typeScoreNotification.length; i++) {
                    if (viewNotification.getText().equals(typeScoreNotification[i])) {
                        checkedItem = i;
                        choiced[0] = typeScoreNotification[i];
                    }
                }
                final int item = checkedItem;
                final int[] checked = new int[1];
                checked[0] = notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)];

                builder.setSingleChoiceItems(typeScoreNotification, notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choiced[0] = typeScoreNotification[which];
                        checked[0] = which;
                    }
                });

                builder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)] = checked[0];
                        viewNotification.setText(choiced[0]);
                        if (checked[0] == 0) {
                            timeN.setVisibility(View.GONE);
                        } else {
                            timeN.setVisibility(View.VISIBLE);
                        }
                        if (checked[0] == 1 && item != 1) {
                            timeN.setText(getString(R.string.beforeWord) + " " + notificationBeforePoint[allNotificationBlocks.indexOfChild(block)] + " " +
                                    variantDeltaTime[notificationBeforeScoreIndex[allNotificationBlocks.indexOfChild(block)]]);
                        } else if (checked[0] == 2 && item != 2) {
                            timeN.setText(getString(R.string.inWord) + " " + notificationTimeExactly[0][allNotificationBlocks.indexOfChild(block)] + ":" +
                                    String.format("%02d", notificationTimeExactly[1][allNotificationBlocks.indexOfChild(block)]));
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        durationN.setOnClickListener(selectIndicationNotification);

        if (notificationScoreTypeIndex[allNotificationBlocks.getChildCount()] == 2) {
            timeN.setText(getString(R.string.inWord) + " " + notificationTimeExactly[0][allNotificationBlocks.getChildCount()] + ":" +
                    String.format("%02d", notificationTimeExactly[1][allNotificationBlocks.getChildCount()]));
        } else if (notificationScoreTypeIndex[allNotificationBlocks.getChildCount()] == 1) {
            timeN.setText(getString(R.string.beforeWord) + " " + notificationBeforePoint[allNotificationBlocks.getChildCount()] + " " +
                    variantDeltaTime[notificationBeforeScoreIndex[allNotificationBlocks.getChildCount()]]);
        } else {
            timeN.setVisibility(View.GONE);
        }
        final int[] checkedVariantDeltaTime = {1};
        View.OnClickListener selectTimeNotification = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDeltaTime = (TextView) v;

                if (notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)] == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RedactKeep.this);
                    builder.setTitle(getString(R.string.setTimeTitleDialog));

                    final EditText deltaTime = new EditText(RedactKeep.this);
                    deltaTime.setInputType(InputType.TYPE_CLASS_NUMBER);
                    deltaTime.setText(String.valueOf(notificationBeforePoint[allNotificationBlocks.indexOfChild(block)]));
                    LinearLayout.LayoutParams deltaTimeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    deltaTimeParams.setMargins((int) (24 * scaleTranslate), 0, (int) (24 * scaleTranslate), 0);
                    LinearLayout deltaTimeLayout = new LinearLayout(RedactKeep.this);
                    deltaTimeLayout.addView(deltaTime, deltaTimeParams);
                    builder.setView(deltaTimeLayout);

                    checkedVariantDeltaTime[0] = notificationBeforeScoreIndex[allNotificationBlocks.indexOfChild(block)];

                    builder.setSingleChoiceItems(variantDeltaTime, notificationBeforeScoreIndex[allNotificationBlocks.indexOfChild(block)], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedVariantDeltaTime[0] = which;
                        }
                    });

                    builder.setPositiveButton(getString(R.string.accessDialogButton), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (deltaTime.getText().toString().equals("") || deltaTime.getText().toString().equals("0")) deltaTime.setText("1");
                            selectedDeltaTime.setText(getString(R.string.beforeWord) + " " + deltaTime.getText().toString() + " " + variantDeltaTime[checkedVariantDeltaTime[0]]);
                            notificationBeforeScoreIndex[allNotificationBlocks.indexOfChild(block)] = checkedVariantDeltaTime[0];
                            notificationBeforePoint[allNotificationBlocks.indexOfChild(block)] = Integer.parseInt(deltaTime.getText().toString());
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    SISTEM_MESSAGE = "set_delta_time_notification";
                    timeHour = notificationTimeExactly[0][notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)]];
                    timeMinute = notificationTimeExactly[1][notificationScoreTypeIndex[allNotificationBlocks.indexOfChild(block)]];
                    usedLastView = block;
                    usedLastTimeView = v;
                    showDialog(DIALOG_TIME);
                }
            }
        };
        timeN.setOnClickListener(selectTimeNotification);

        Button repeatN = block.findViewById(R.id.period_repeat_event);
        repeatN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedLastView = v;

                usedLastBlock = (LinearLayout)(v.getParent());
                SISTEM_NUMBER = ((LinearLayout)(usedLastBlock.getParent())).indexOfChild(usedLastBlock);
                periodRepeatEventEditText.setText(String.valueOf(notificationRereatedPoint[SISTEM_NUMBER]));
                typeRepeatTimeSeekBar.setProgress(notificationRereatedScoreVariantNumber[SISTEM_NUMBER]);
                setTypeRepeatTimeTextView(notificationRereatedScoreVariantNumber[SISTEM_NUMBER]);
                for (int i = 0; i < 7; i++) {
                    if (notificationRereatedDayOfWeak[SISTEM_NUMBER][i]) {
                        ((LinearLayout) dialogRepeatTime.findViewById(R.id.set_repeat_time_only_days_linear_layout)).getChildAt(i).setBackgroundResource(R.color.colorLightBlueKeep);
                    } else {
                        ((LinearLayout) dialogRepeatTime.findViewById(R.id.set_repeat_time_only_days_linear_layout)).getChildAt(i).setBackgroundResource(R.color.colorWhiteKeep);
                    }
                }
                arrayRereatedDayOfWeak = notificationRereatedDayOfWeak[SISTEM_NUMBER];
                arrayRereatedEnd = notificationRereatedEnd[SISTEM_NUMBER];
                arrayRereatedTimeInterval = notificationRereatedTimeInterval[SISTEM_NUMBER];
                ((TextView) dialogRepeatTime.findViewById(R.id.only_times_start_interval)).setText(notificationRereatedTimeInterval[SISTEM_NUMBER][0] + ":" + String.format("%02d", notificationRereatedTimeInterval[SISTEM_NUMBER][1]));
                ((TextView) dialogRepeatTime.findViewById(R.id.only_times_end_interval)).setText(notificationRereatedTimeInterval[SISTEM_NUMBER][2] + ":" + String.format("%02d", notificationRereatedTimeInterval[SISTEM_NUMBER][3]));
                switch (notificationRereatedEndVariantNumber[SISTEM_NUMBER]) {
                    case 0:
                        TYPE_REPEAT_EVENT = 0;
                        ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(true);
                        ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(false);
                        ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                        break;
                    case 1:
                        TYPE_REPEAT_EVENT = 1;
                        ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(false);
                        ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(true);
                        ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(false);
                        break;
                    case 2:
                        TYPE_REPEAT_EVENT = 2;
                        ((RadioButton) typeRepeatEventNever.getChildAt(0)).setChecked(false);
                        ((RadioButton) typeRepeatEventDate.getChildAt(0)).setChecked(false);
                        ((RadioButton) typeRepeatEventTimes.getChildAt(0)).setChecked(true);
                        break;
                }
                typeRepeatEventDateText.setText(String.format("%02d", notificationRereatedEnd[SISTEM_NUMBER][0]) + "." +
                        String.format("%02d", notificationRereatedEnd[SISTEM_NUMBER][1] + 1) + "." + notificationRereatedEnd[SISTEM_NUMBER][2]);
                typeRepeatEventTimesNumber.setText(String.valueOf(notificationRereatedEnd[SISTEM_NUMBER][3]));

                repeatTimeDialog.show();
            }
        });
        notificationBlock[allNotificationBlocks.getChildCount()] = block;
        allNotificationBlocks.addView(block);
        SISTEM_NUMBER = allNotificationBlocks.getChildCount() - 1;
        AllTimeFunction.SetTextRepeat(repeatN);
    }

    public void createTableFunction() {
        final float scaleTranslate = getResources().getDisplayMetrics().density;

        final LinearLayout createdTable = new LinearLayout(this);
        LinearLayout.LayoutParams createTableLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        createdTable.setOrientation(LinearLayout.VERTICAL);
        List<RelativeLayout> tableItemsArray = new ArrayList<RelativeLayout>();

        final LinearLayout tableItems = new LinearLayout(this);
        LinearLayout.LayoutParams tableItemsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tableItems.setOrientation(LinearLayout.VERTICAL);
        createdTable.addView(tableItems, tableItemsLayoutParams);

        LayoutInflater itemTableInflater = getLayoutInflater();
        final RelativeLayout itemTable = (RelativeLayout) itemTableInflater.inflate(R.layout.activity_redact_keep_item_table, null);
        final EditText itemTableText = itemTable.findViewById(R.id.item_table_text);
        if (RedactKeep.fontFamilyPathKeep.equals("Sans-Serif")) {
            itemTableText.setTypeface(Typeface.SANS_SERIF);
        } else {
            itemTableText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + RedactKeep.fontFamilyPathKeep));
        }
        itemTableText.setTextSize((float)RedactKeep.textSizeKeep);
        tableItemsArray.add(itemTable);
        tableItems.addView(itemTable);
        itemTable.requestFocus();

        TextView addItemTableButton = new TextView(this);
        addItemTableButton.setText(R.string.add_item_table);
        addItemTableButton.setTextSize(18);
        addItemTableButton.setPadding((int) (3 * scaleTranslate), (int) (2 * scaleTranslate), 0, 0);
        addItemTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater itemTableInflater = getLayoutInflater();
                final RelativeLayout itemTable = (RelativeLayout) itemTableInflater.inflate(R.layout.activity_redact_keep_item_table, null);
                final EditText itemTableText = itemTable.findViewById(R.id.item_table_text);
                final int NumberTable = TablesItems.size() - 1;
                if (RedactKeep.fontFamilyPathKeep.equals("Sans-Serif")) {
                    itemTableText.setTypeface(Typeface.SANS_SERIF);
                } else {
                    itemTableText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + RedactKeep.fontFamilyPathKeep));
                }
                itemTableText.setTextSize((float)RedactKeep.textSizeKeep);
                TablesItems.get(NumberTable).add(itemTable);
                itemTable.getChildAt(0).setVisibility(((RelativeLayout)tableItems.getChildAt(0)).getChildAt(0).getVisibility());
                tableItems.addView(itemTable);
                itemTable.requestFocus();
            }
        });
        createdTable.addView(addItemTableButton);

        TablesItems.add((ArrayList<RelativeLayout>) tableItemsArray);
        RedactKeep.mainLayoutRedactKeep.addView(createdTable, createTableLayoutParams);

        EditText newParagraph = new EditText(this);
        newParagraph.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        if (RedactKeep.fontFamilyPathKeep.equals("Sans-Serif")) {
            newParagraph.setTypeface(Typeface.SANS_SERIF);
        } else {
            newParagraph.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + RedactKeep.fontFamilyPathKeep));
        }
        newParagraph.setTextSize((float)RedactKeep.textSizeKeep);
        newParagraph.setBackgroundResource(android.R.color.transparent);
        RedactKeep.mainLayoutRedactKeep.addView(newParagraph);
    }

    public void makeCheck(MenuItem item) {
        RelativeLayout focusedTextLayout = (RelativeLayout) RedactKeep.this.getCurrentFocus().getParent();
        if (focusedTextLayout.getChildAt(0).getVisibility() == View.VISIBLE) {
            for (int i = 0; i < ((LinearLayout) focusedTextLayout.getParent()).getChildCount(); i++) {
                ((RelativeLayout)((LinearLayout) focusedTextLayout.getParent()).getChildAt(i)).getChildAt(0).setVisibility(View.GONE);
                ((EditText)((RelativeLayout)((LinearLayout) focusedTextLayout.getParent()).getChildAt(i))
                        .getChildAt(1)).setPaintFlags(0);;
                ((EditText)((RelativeLayout)((LinearLayout) focusedTextLayout.getParent()).getChildAt(i))
                        .getChildAt(1)).setTextColor(ContextCompat.getColor(this, R.color.colorZDefaultText));
            }
        } else {
            for (int i = 0; i < ((LinearLayout) focusedTextLayout.getParent()).getChildCount(); i++) {
                ((RelativeLayout)((LinearLayout) focusedTextLayout.getParent()).getChildAt(i)).getChildAt(0).setVisibility(View.VISIBLE);
            }
        }
    }

    public void ChangeCheck(View view) {
        if(((CheckBox)view).isChecked()) {
            ((EditText)((RelativeLayout)view.getParent()).getChildAt(1)).setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ((EditText)((RelativeLayout)view.getParent()).getChildAt(1)).setTextColor(ContextCompat.getColor(this, R.color.colorZCheckedText));
        } else {
            ((EditText)((RelativeLayout)view.getParent()).getChildAt(1)).setPaintFlags(0);
            ((EditText)((RelativeLayout)view.getParent()).getChildAt(1)).setTextColor(ContextCompat.getColor(this, R.color.colorZDefaultText));
        }
    }

    public void MakeCopyKeep(MenuItem item) {
        String keepNameCopy = keepName;
        keepName = "newKeep";
        allRedactFunction.saveFullKeep(dbHelper, this);
        keepName = keepNameCopy;

        Toast toast = Toast.makeText(getApplicationContext(),
                "Копия заметки создана.", Toast.LENGTH_SHORT);
        toast.show();
    }
}