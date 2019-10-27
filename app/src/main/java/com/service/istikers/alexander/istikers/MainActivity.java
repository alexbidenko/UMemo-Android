package com.service.istikers.alexander.istikers;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.service.istikers.alexander.istikers.DBHelper.KEY_KEEP_NAME;
import static com.service.istikers.alexander.istikers.DBHelper.TABLE_KEEPS;
import static com.service.istikers.alexander.istikers.RedactKeep.NOTIFICATION_KEEP_ADD_KEY;
import static com.service.istikers.alexander.istikers.RedactKeep.TEXT_KEEP_ADD_KEY;
import static com.service.istikers.alexander.istikers.RedactKeep.keepName;

public class MainActivity extends AppCompatActivity {
    LinearLayout first_col, second_col;

    public static final String NAME_OF_KEEP = "com.service.istikers.alexander.nameOfKeep";

    DBHelper dbHelper;

    List<View> stikerList = new ArrayList<View>();
    List<String> stikerTitleList = new ArrayList<String>();
    List<String> stikerTextList = new ArrayList<String>();
    List<Boolean> stikerEventDate = new ArrayList<Boolean>();
    List<Boolean> stikerEventTomorow = new ArrayList<Boolean>();
    List<Integer> stikerColor = new ArrayList<Integer>();
    List<Boolean> stikerIsMore = new ArrayList<Boolean>();
    List<String> emitKeeps = new ArrayList<String>();

    String login;
    String pass;

    View navLayout;
    EditText searchedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emitKeeps.clear();
        searchedText = (EditText) findViewById(R.id.searched_text);

        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
        login = sPref.getString("login", "");
        pass = sPref.getString("pass", "");

        navLayout = getLayoutInflater().inflate(R.layout.activity_main_navigation_content, null);
        Button allKeep = (Button) navLayout.findViewById(R.id.all_keeps);
        Button todayKeep = (Button) navLayout.findViewById(R.id.today_keeps);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(navLayout);

        if (!login.equals("") && !pass.equals("")) {
            Button singIn = (Button) navLayout.findViewById(R.id.sing_in);
            singIn.setText(login);
        }

        searchedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(whichFilter == 0) {
                    whichFilter = 1;
                }
                FilterKeeps();
            }
        });
    }

    boolean UpdataOrDownload = false;

    private Handler TimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (UpdataOrDownload) {
                if (!login.equals("") && !pass.equals("")) {
                    SendPost send = new SendPost(MainActivity.this);
                    send.execute("getWeb", login, pass);
                }
                UpdataOrDownload = false;
            } else {
                updata();
                UpdataOrDownload = true;
            }
            TimeHandler.sendEmptyMessageDelayed(0,7000L);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        searchedText.setVisibility(View.GONE);

        updata();

        UpdataOrDownload = false;
        TimeHandler.sendEmptyMessageDelayed(0,1000L);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TimeHandler.removeMessages(0);
    }

    static boolean isLong = false;

    void updata() {
        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
        String login = sPref.getString("login", "");
        String pass = sPref.getString("pass", "");

        int first_col_length = 0;
        int second_col_length = 0;
        first_col = (LinearLayout) findViewById(R.id.first_col);
        second_col = (LinearLayout) findViewById(R.id.second_col);

        first_col.removeAllViews();
        second_col.removeAllViews();

        stikerList.clear();
        stikerTitleList.clear();
        stikerTextList.clear();
        stikerEventDate.clear();
        stikerEventTomorow.clear();
        stikerColor.clear();

        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(TABLE_KEEPS, null, null, null, null, null, null);

        if (cursor.moveToLast()) {
            int textKeepGenerateIndex = cursor.getColumnIndex(KEY_KEEP_NAME);

            do {
                LayoutInflater stikerInflater = getLayoutInflater();
                final View stikerLayout = stikerInflater.inflate(R.layout.activity_main_stiker, null);
                LinearLayout stiker = (LinearLayout) stikerLayout.findViewById(R.id.stiker);
                TextView stikerTitle = (TextView) stikerLayout.findViewById(R.id.stiker_title);
                TextView stikerText = (TextView) stikerLayout.findViewById(R.id.stiker_text);
                final TextView stikerDate = (TextView) stikerLayout.findViewById(R.id.stiker_date);
                ImageButton stikerMore = (ImageButton) stikerLayout.findViewById(R.id.stiker_more);
                final LinearLayout stikerMoreArea = (LinearLayout) stikerLayout.findViewById(R.id.stiker_more_area);

                String textKeepGenerate = "";
                final String nameKeepTable = cursor.getString(textKeepGenerateIndex);
                Cursor cursorKeep = database.query(nameKeepTable + TEXT_KEEP_ADD_KEY, null, null, null, null, null, null);

                if (cursorKeep.moveToFirst()) {
                    do {
                        Map<String, String> mapKeep = new HashMap<String, String>();
                        mapKeep.put(
                                cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)),
                                cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));

                        if (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_IS_NOTIFY)) {
                            cursorKeep.moveToLast();
                        } else {
                            if (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_TITLE)) {
                                if (!cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("")) {
                                    stikerTitle.setText(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                                    stikerTitle.setVisibility(View.VISIBLE);
                                }
                                stikerTitleList.add(cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                            } else {
                                if (!textKeepGenerate.equals("") && !(
                                        cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("") && cursorKeep.isLast())) {
                                    textKeepGenerate += "\n";
                                }
                                if (cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_TYPE_OF_DATA)).equals(DBHelper.TYPE_DATA_TABLE_ITEM)) {
                                    String text = cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                    if (text.length() > 2) {
                                        if (text.substring(0, 3).equals("&t$")) {
                                            textKeepGenerate += "\u2714 ";
                                            SpannableStringBuilder string = new SpannableStringBuilder(text.substring(3));
                                            string.setSpan(new StrikethroughSpan(), 0, string.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorZCheckedText)), 0, string.length(), 0);
                                            textKeepGenerate += string;
                                        } else if (text.substring(0, 3).equals("&f$")) {
                                            textKeepGenerate += "\u2014 ";
                                            textKeepGenerate += text.substring(3);
                                        } else {
                                            textKeepGenerate += "\u2022 ";
                                            textKeepGenerate += text;
                                        }
                                    } else {
                                        textKeepGenerate += "\u2022 ";
                                        textKeepGenerate += text;
                                    }
                                } else {
                                    textKeepGenerate += cursorKeep.getString(cursorKeep.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA));
                                }
                            }
                        }
                    } while (cursorKeep.moveToNext());
                }

                Calendar now = Calendar.getInstance();
                Calendar event = Calendar.getInstance();
                if (cursor.getLong(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME)) != 0) {
                    event.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME)));
                    if (event.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
                            event.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            event.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                        stikerEventDate.add(true);
                    } else stikerEventDate.add(false);
                } else {
                    Cursor cursorNotification = database.query(nameKeepTable + NOTIFICATION_KEEP_ADD_KEY, null,
                            null, null, null, null, null);
                    cursorNotification.moveToPosition(0);
                    boolean isNotifed = false;
                    if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("true")) isNotifed = true;
                    cursorNotification.moveToPosition(1);
                    try {
                        JSONObject jsonObject = new JSONObject(cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                        int day = jsonObject.getInt(DBHelper.JSON_KEY_DAY);
                        int month = jsonObject.getInt(DBHelper.JSON_KEY_MONTH);
                        int year = jsonObject.getInt(DBHelper.JSON_KEY_YEAR);
                        if (now.get(Calendar.DAY_OF_MONTH) == day &&
                                now.get(Calendar.MONTH) == month &&
                                now.get(Calendar.YEAR) == year && isNotifed) stikerEventDate.add(true);
                        else stikerEventDate.add(false);
                    } catch (Exception e) {}
                }

                if (cursor.getLong(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME)) != 0) {
                    event.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DBHelper.KEY_NEXT_EVENT_KEEP_NAME)));
                    if (event.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) + 1 &&
                            event.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                            event.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                        stikerEventTomorow.add(true);
                    } else stikerEventTomorow.add(false);
                } else {
                    Cursor cursorNotification = database.query(nameKeepTable + NOTIFICATION_KEEP_ADD_KEY, null,
                            null, null, null, null, null);
                    cursorNotification.moveToPosition(0);
                    boolean isNotifed = false;
                    if (cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)).equals("true")) isNotifed = true;
                    cursorNotification.moveToPosition(1);
                    try {
                        JSONObject jsonObject = new JSONObject(cursorNotification.getString(cursorNotification.getColumnIndex(DBHelper.KEEP_VALUE_OF_DATA)));
                        int day = jsonObject.getInt(DBHelper.JSON_KEY_DAY);
                        int month = jsonObject.getInt(DBHelper.JSON_KEY_MONTH);
                        int year = jsonObject.getInt(DBHelper.JSON_KEY_YEAR);
                        if (now.get(Calendar.DAY_OF_MONTH) + 1 == day &&
                                now.get(Calendar.MONTH) == month &&
                                now.get(Calendar.YEAR) == year && isNotifed) stikerEventTomorow.add(true);
                        else stikerEventTomorow.add(false);
                    } catch (Exception e) {}
                }

                final float scaleTranslate = getResources().getDisplayMetrics().density;

                JSONObject jsonObject;
                int colorKeep = R.color.colorWhiteKeep;
                String dateRedactKeep = null;
                try {
                    jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PARAMS_KEEP_NAME)));
                    colorKeep = jsonObject.getInt(DBHelper.JSON_KEY_COLOR_KEEP);
                    if (jsonObject.getString(DBHelper.JSON_KEY_FONT_FAMILY_PATH_KEEP).equals("Sans-Serif")) {
                        stikerText.setTypeface(Typeface.SANS_SERIF);
                        stikerTitle.setTypeface(Typeface.SANS_SERIF);
                    } else {
                        stikerText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + jsonObject.getString(DBHelper.JSON_KEY_FONT_FAMILY_PATH_KEEP)));
                        stikerTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + jsonObject.getString(DBHelper.JSON_KEY_FONT_FAMILY_PATH_KEEP)));
                    }
                    stikerText.setTextSize((float)(jsonObject.getInt(DBHelper.JSON_KEY_TEXT_SIZE_KEEP) * 3 / 4));
                    stikerTitle.setTextSize((float)(jsonObject.getInt(DBHelper.JSON_KEY_TEXT_SIZE_KEEP) * 1.1));
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(jsonObject.getLong(DBHelper.JSON_KEY_DATE_REDACT));
                    int dateYearRedact = c.get(Calendar.YEAR);
                    int dateMonthRedact = c.get(Calendar.MONTH) + 1;
                    int dateDayRedact = c.get(Calendar.DAY_OF_MONTH);
                    int dateHourRedact = c.get(Calendar.HOUR_OF_DAY);
                    int dateMinuteRedact = c.get(Calendar.MINUTE);
                    dateRedactKeep = dateDayRedact + "." + String.format("%02d", dateMonthRedact) + "." + dateYearRedact + " " + dateHourRedact + ":" +
                            String.format("%02d", dateMinuteRedact);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stikerText.setText(textKeepGenerate);
                stikerTextList.add(textKeepGenerate);
                stikerDate.setText(dateRedactKeep);
                final Resources res = getResources();
                if(!emitKeeps.contains(nameKeepTable)) {
                    Drawable material = res.getDrawable(R.drawable.material_white);
                    material.setColorFilter(ContextCompat.getColor(MainActivity.this, colorKeep), PorterDuff.Mode.SRC_ATOP);
                    stiker.setBackground(material);
                    stiker.setElevation((int)(1 * scaleTranslate));
                } else {
                    int orColor = ContextCompat.getColor(MainActivity.this, colorKeep);
                    Drawable emit =  res.getDrawable(R.drawable.material_emit);
                    int newColor = Color.rgb(Color.red(orColor) - 33, Color.green(orColor) - 33, Color.blue(orColor) - 33);
                    emit.setColorFilter(newColor, PorterDuff.Mode.MULTIPLY);
                    stiker.setBackground(null);
                    stiker.setBackground(emit);
                    stiker.setElevation((int)(4 * scaleTranslate));
                }
                stikerColor.add(colorKeep);

                stikerMore.setOnClickListener(new View.OnClickListener() {
                    int index = stikerList.size();

                    @Override
                    public void onClick(View v) {
                        if (!stikerIsMore.get(index)) {
                            ((TextView) ((RelativeLayout) v.getParent()).getChildAt(1)).setMaxHeight((int) (500 * scaleTranslate));
                            v.setRotation(180);
                            stikerMoreArea.setVisibility(View.VISIBLE);
                            stikerIsMore.set(index, true);
                        } else {
                            ((TextView) ((RelativeLayout) v.getParent()).getChildAt(1)).setMaxHeight((int) (160 * scaleTranslate));
                            v.setRotation(0);
                            stikerMoreArea.setVisibility(View.GONE);
                            stikerIsMore.set(index, false);
                        }
                    }
                });

                final Intent intent = new Intent(this, RedactKeep.class);

                final ConstraintLayout activityMain = (ConstraintLayout) findViewById(R.id.activity_main);

                final int color = colorKeep;
                View.OnClickListener toKeep = new View.OnClickListener() {
                    String keep = nameKeepTable;
                    int orColor = ContextCompat.getColor(MainActivity.this, color);

                    @Override
                    public void onClick(View v) {
                        if(!isLong) {
                            if (emitKeeps.isEmpty()) {
                                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this,
                                        R.animator.click_material_anim);
                                set.setTarget((LinearLayout) ((RelativeLayout) ((TextView) v).getParent()).getParent());
                                set.start();

                                intent.putExtra(NAME_OF_KEEP, nameKeepTable);
                                startActivity(intent);
                            } else {
                                v = (View) v.getParent().getParent();
                                if (!emitKeeps.contains(keep)) {
                                    emitKeeps.add(keep);

                                    Drawable emit = res.getDrawable(R.drawable.material_emit);
                                    int newColor = Color.rgb(Color.red(orColor) - 33, Color.green(orColor) - 33, Color.blue(orColor) - 33);
                                    emit.setColorFilter(newColor, PorterDuff.Mode.MULTIPLY);
                                    v.setBackground(null);
                                    v.setBackground(emit);
                                    v.setElevation((int)(4 * scaleTranslate));

                                    if (!emitKeeps.isEmpty()) {
                                        findViewById(R.id.clasic_top_panel).setVisibility(View.GONE);
                                        findViewById(R.id.emit_top_panel).setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    emitKeeps.remove(keep);

                                    Drawable material = res.getDrawable(R.drawable.material_white);
                                    material.setColorFilter(ContextCompat.getColor(MainActivity.this, color), PorterDuff.Mode.SRC_ATOP);
                                    v.setBackground(material);
                                    v.setElevation((int)(1 * scaleTranslate));

                                    if (emitKeeps.isEmpty()) {
                                        findViewById(R.id.emit_top_panel).setVisibility(View.GONE);
                                        findViewById(R.id.clasic_top_panel).setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } else isLong = false;
                    }
                };
                View.OnLongClickListener emitKeep = new View.OnLongClickListener() {
                    String keep = nameKeepTable;
                    int orColor = ContextCompat.getColor(MainActivity.this, color);

                    @Override
                    public boolean onLongClick(View v) {
                        isLong = true;
                        emitKeeps.add(keep);

                        v = (View)v.getParent().getParent();
                        Drawable emit =  res.getDrawable(R.drawable.material_emit);
                        int newColor = Color.rgb(Color.red(orColor) - 33, Color.green(orColor) - 33, Color.blue(orColor) - 33);
                        emit.setColorFilter(newColor, PorterDuff.Mode.MULTIPLY);
                        v.setBackground(null);
                        v.setBackground(emit);
                        v.setElevation((int)(4 * scaleTranslate));

                        if(!emitKeeps.isEmpty()) {
                            findViewById(R.id.clasic_top_panel).setVisibility(View.GONE);
                            findViewById(R.id.emit_top_panel).setVisibility(View.VISIBLE);
                        }

                        return false;
                    }
                };
                stikerText.setOnLongClickListener(emitKeep);
                stikerTitle.setOnLongClickListener(emitKeep);
                stikerText.setOnClickListener(toKeep);
                stikerTitle.setOnClickListener(toKeep);
                stikerList.add(stikerLayout);

                if(stikerIsMore.size() < stikerList.size()) {
                    stikerIsMore.add(false);
                } else {
                    if (stikerIsMore.get(stikerList.size() - 1)) {
                        stikerText.setMaxHeight((int) (500 * scaleTranslate));
                        stikerMore.setRotation(180);
                        stikerMoreArea.setVisibility(View.VISIBLE);
                    }
                }

                if (first_col_length <= second_col_length) {
                    first_col.addView(stikerLayout);
                    if (textKeepGenerate.length() < 70) {
                        first_col_length += textKeepGenerate.length();
                    } else {
                        first_col_length += 70;
                    }
                } else {
                    second_col.addView(stikerLayout);
                    if (textKeepGenerate.length() < 70) {
                        second_col_length += textKeepGenerate.length();
                    } else {
                        second_col_length += 70;
                    }
                }
                cursorKeep.close();
            } while (cursor.moveToPrevious());
        }

        cursor.close();

        dbHelper.close();

        FilterKeeps();
    }

    public void toCreateKeep(View view) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this,
                R.animator.click_material_anim);
        set.setTarget(findViewById(R.id.createKeep));

        set.start();

        Intent intent = new Intent(this, RedactKeep.class);
        intent.putExtra(NAME_OF_KEEP, "newKeep");
        startActivity(intent);
    }

    public void searchKeep(View view) {
        searchedText.setVisibility(View.VISIBLE);
        searchedText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchedText, 0);
        }
    }

    void FilterKeeps () {
        String searched = searchedText.getText().toString().toLowerCase();
        for (int i = 0; i < stikerList.size(); i++) {
            stikerList.get(i).setVisibility(View.VISIBLE);
        }
        if (whichFilter > 0) {
            for (int i = 0; i < stikerList.size(); i++) {
                if (stikerTitleList.get(i).toLowerCase().contains(searched) ||
                        stikerTextList.get(i).toLowerCase().contains(searched)) {
                    stikerList.get(i).setVisibility(View.VISIBLE);
                } else {
                    stikerList.get(i).setVisibility(View.GONE);
                }
            }
            if(whichFilter == 2) {
                for (int i = 0; i < stikerList.size(); i++) stikerList.get(i).setVisibility(View.VISIBLE);
                for (int i = 0; i < stikerList.size(); i++) {
                    if (!stikerEventDate.get(i)) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                }
            } else if(whichFilter == 3) {
                for (int i = 0; i < stikerList.size(); i++) stikerList.get(i).setVisibility(View.VISIBLE);
                for (int i = 0; i < stikerList.size(); i++) {
                    if (!stikerEventTomorow.get(i)) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                }
            }
        }
        for (int i = 0; i < stikerList.size(); i++) {
            switch (stikerColor.get(i)) {
                case R.color.colorWhiteKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_white)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorRedKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_red)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorOrangeKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_orange)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorYellowKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_yellow)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorGreenKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_green)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorLightBlueKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_light_blue)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorBlueKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_blue)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
                case R.color.colorPurpurKeep:
                case R.color.colorFuchsiaKeep:
                    if(!((CheckBox)navLayout.findViewById(R.id.only_purpur)).isChecked()) {
                        stikerList.get(i).setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    int whichFilter = 0;

    public void allKeeps(View view) {
        whichFilter = 0;
        FilterKeeps ();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void todayKeeps(View view) {
        whichFilter = 2;
        FilterKeeps ();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void tomorowKeeps(View view) {
        whichFilter = 3;
        FilterKeeps ();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void ChangeOnly(View view) {
        FilterKeeps ();
    }

    public void openNavigationview(View view) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    public void singIn(View view) {
        Intent toProfile = new Intent(this, Profile.class);
        startActivity(toProfile);
    }

    public void DeleteEmitKeeps(View view) {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(this);
        DialogBuilder.setTitle("Удалить выделенные заметки?");
        DialogBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
                for (int i = 0; i < emitKeeps.size(); i++) {
                    dataBase.delete(TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{emitKeeps.get(i)});
                    dataBase.delete(emitKeeps.get(i) + TEXT_KEEP_ADD_KEY, null, null);
                    dataBase.delete(emitKeeps.get(i) + NOTIFICATION_KEEP_ADD_KEY, null, null);
                    try {
                        dataBase.delete(emitKeeps.get(i) + SearchNextNotify.NEXT_NOTIFY_ADD_KEY, null, null);
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

                SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
                String login = sPref.getString("login", "");
                String pass = sPref.getString("pass", "");
                if (!login.equals("") && !pass.equals("")) {
                    SendPost send = new SendPost(MainActivity.this);
                    send.execute("saveKeeps", login, pass, String.valueOf(new JSONObject(mapAllKeeps)));
                }

                dataBase.close();

                findViewById(R.id.emit_top_panel).setVisibility(View.GONE);
                findViewById(R.id.clasic_top_panel).setVisibility(View.VISIBLE);

                updata();
            }
        });
        DialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog Dialog = DialogBuilder.create();
        Dialog.show();
    }

    public void ClearEmitKeeps(View view) {
        emitKeeps.clear();

        findViewById(R.id.emit_top_panel).setVisibility(View.GONE);
        findViewById(R.id.clasic_top_panel).setVisibility(View.VISIBLE);

        updata();
    }
}
