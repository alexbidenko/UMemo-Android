package com.service.istikers.alexander.istikers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.service.istikers.alexander.istikers.DBHelper.TABLE_KEEPS;
import static com.service.istikers.alexander.istikers.RedactKeep.TEXT_KEEP_ADD_KEY;

public class Profile extends AppCompatActivity {

    String login;
    String pass;

    TextView errorText;
    TextView newErrorText;

    EditText loginEdit;
    EditText passEdit;
    EditText newLoginEdit;
    EditText newPassEdit;
    EditText newRepeatPassEdit;
    EditText newEmailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        errorText = (TextView) findViewById(R.id.error);
        newErrorText = (TextView) findViewById(R.id.new_error);

        loginEdit = (EditText) findViewById(R.id.login);
        passEdit = (EditText) findViewById(R.id.pass);
        newLoginEdit = (EditText) findViewById(R.id.new_login);
        newPassEdit = (EditText) findViewById(R.id.new_pass);
        newRepeatPassEdit = (EditText) findViewById(R.id.new_repeat_pass);
        newEmailEdit = (EditText) findViewById(R.id.new_email);

        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
        login = sPref.getString("login", "");
        pass = sPref.getString("pass", "");

        if (!login.equals("") && !pass.equals("")) {
            ((TextView)findViewById(R.id.login_profile)).setText(login);
            findViewById(R.id.to_profile).setVisibility(View.GONE);
            findViewById(R.id.is_profile).setVisibility(View.VISIBLE);
        }
    }

    public void wantEnter(View view) {
        errorText.setVisibility(View.GONE);
        findViewById(R.id.want_regist).setVisibility(View.GONE);
        findViewById(R.id.want_enter).setVisibility(View.VISIBLE);
    }

    public void wantRegist(View view) {
        newErrorText.setVisibility(View.GONE);
        findViewById(R.id.want_enter).setVisibility(View.GONE);
        findViewById(R.id.want_regist).setVisibility(View.VISIBLE);
    }

    public void Regist(View view) {
        newErrorText.setVisibility(View.GONE);

        Pattern p = Pattern.compile("[A-Za-z0-9]{6,}");
        if (newPassEdit.getText().toString().equals(newRepeatPassEdit.getText().toString())) {
            if (p.matcher(newLoginEdit.getText().toString()).matches() && p.matcher(newPassEdit.getText().toString()).matches()) {
                SendPost send = new SendPost(this);
                send.execute("regist", newLoginEdit.getText().toString(), newPassEdit.getText().toString(), newEmailEdit.getText().toString());

                SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.putString("login", newLoginEdit.getText().toString());
                editor.putString("pass", newPassEdit.getText().toString());
                editor.putString("email", newEmailEdit.getText().toString());
                editor.apply();

                try {
                    TimeUnit.MILLISECONDS.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                login = sPref.getString("login", "");
                pass = sPref.getString("pass", "");

                if (!login.equals("") && !pass.equals("")) {
                    send = new SendPost(this);
                    send.execute("getWeb", newLoginEdit.getText().toString(), newPassEdit.getText().toString());

                    ((TextView)findViewById(R.id.login_profile)).setText(login);
                    findViewById(R.id.to_profile).setVisibility(View.GONE);
                    findViewById(R.id.is_profile).setVisibility(View.VISIBLE);

                    TimeHandler.sendEmptyMessageDelayed(1,1500L);
                } else {
                    newErrorText.setText("Такой логин уже занят.");
                    newErrorText.setVisibility(View.VISIBLE);
                }
            } else {
                newErrorText.setText("Логин и пароль должны содержать не менее 6 символов латинского алфавита или цифр.");
                newErrorText.setVisibility(View.VISIBLE);
            }
        } else {
            newErrorText.setText("Пароли не совпадают.");
            newErrorText.setVisibility(View.VISIBLE);
        }
    }

    public void Enter(View view) {
        errorText.setVisibility(View.GONE);

        SendPost send = new SendPost(this);
        send.execute("singIn", loginEdit.getText().toString(), passEdit.getText().toString());

        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("login", loginEdit.getText().toString());
        editor.putString("pass", passEdit.getText().toString());
        editor.apply();

        try {
            TimeUnit.MILLISECONDS.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        login = sPref.getString("login", "");
        pass = sPref.getString("pass", "");

        if (!login.equals("") && !pass.equals("")) {
            send = new SendPost(this);
            send.execute("getWeb", loginEdit.getText().toString(), passEdit.getText().toString());

            ((TextView)findViewById(R.id.login_profile)).setText(login);
            findViewById(R.id.to_profile).setVisibility(View.GONE);
            findViewById(R.id.is_profile).setVisibility(View.VISIBLE);

            TimeHandler.sendEmptyMessageDelayed(1,1500L);
        } else {
            errorText.setText("Неверный логин или пароль.");
            errorText.setVisibility(View.VISIBLE);
        }
    }

    public void SingOut(View view) {
        SharedPreferences sPref = getSharedPreferences("com.service.istikers.alexander.istikers", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.remove("login");
        editor.remove("pass");
        editor.apply();

        findViewById(R.id.is_profile).setVisibility(View.GONE);
        findViewById(R.id.to_profile).setVisibility(View.VISIBLE);
    }

    public void BackActivity(View view) {
        Intent toProfile = new Intent(this, MainActivity.class);
        startActivity(toProfile);
    }

    void SaveKeeps () {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
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
                mapKeeps.put("table_keep_text", String.valueOf(new JSONArray(dataKeep)));

                mapAllKeeps.put(nameKeepTable, mapKeeps);

                cursorKeep.close();
            } while (cursor.moveToNext());
        }

        SendPost send = new SendPost(this);
        send.execute("saveKeeps", login, pass, String.valueOf(new JSONObject(mapAllKeeps)));

        cursor.close();
        dataBase.close();
    }

    @SuppressLint("HandlerLeak")
    private Handler TimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            SendPost send = new SendPost(Profile.this);
            send.execute("getParams", loginEdit.getText().toString(), passEdit.getText().toString());

            try {
                TimeUnit.MILLISECONDS.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SaveKeeps();
        }
    };
}
