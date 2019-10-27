package com.service.istikers.alexander.istikers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static com.service.istikers.alexander.istikers.DBHelper.KEY_KEEP_NAME;
import static com.service.istikers.alexander.istikers.RedactKeep.NOTIFICATION_KEEP_ADD_KEY;
import static com.service.istikers.alexander.istikers.RedactKeep.TEXT_KEEP_ADD_KEY;

public class SendPost extends AsyncTask<String, Void, ArrayList<String>> {

    private Context Con;

    public SendPost (Context context){
        Con = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(String... passing) {
        HttpPost http;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            List nameValuePairs = new ArrayList();
            if (passing[0].equals("singIn")) {
                http = new HttpPost("https://web-umemo.ru/singIn.php");
                nameValuePairs.add(new BasicNameValuePair("comand", "enter"));
                nameValuePairs.add(new BasicNameValuePair("login", passing[1]));
                nameValuePairs.add(new BasicNameValuePair("pass", passing[2]));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());
                if (response.equals("not_pass")) {
                    SharedPreferences sPref = Con.getSharedPreferences("com.service.istikers.alexander.istikers", Con.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.remove("login");
                    editor.remove("pass");
                    editor.commit();
                } else {
                    SharedPreferences sPref = Con.getSharedPreferences("com.service.istikers.alexander.istikers", Con.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("login", passing[1]);
                    editor.putString("pass", passing[2]);
                    editor.commit();
                }
            } else if (passing[0].equals("regist")) {
                http = new HttpPost("https://web-umemo.ru/singIn.php");
                nameValuePairs.add(new BasicNameValuePair("comand", "regist"));
                nameValuePairs.add(new BasicNameValuePair("login", passing[1]));
                nameValuePairs.add(new BasicNameValuePair("pass", passing[2]));
                nameValuePairs.add(new BasicNameValuePair("email", passing[3]));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());
                if (response.equals("no")) {
                    SharedPreferences sPref = Con.getSharedPreferences("com.service.istikers.alexander.istikers", Con.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.remove("login");
                    editor.remove("pass");
                    editor.remove("email");
                    editor.apply();
                } else {
                    SharedPreferences sPref = Con.getSharedPreferences("com.service.istikers.alexander.istikers", Con.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("login", passing[1]);
                    editor.putString("pass", passing[2]);
                    editor.putString("email", passing[3]);
                    editor.apply();
                }
            } else if(passing[0].equals("saveKeeps")) {
                http = new HttpPost("https://web-umemo.ru/add_keep.php");
                nameValuePairs.add(new BasicNameValuePair("login", passing[1]));
                nameValuePairs.add(new BasicNameValuePair("pass", passing[2]));
                String s = passing[3];
                nameValuePairs.add(new BasicNameValuePair("keepData", new String(s.getBytes("UTF-8"), "ISO-8859-1")));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());
                Log.d("response", "doInBackground: " + response);
            } else if(passing[0].equals("getWeb")) {
                http = new HttpPost("https://web-umemo.ru/get_keeps.php");
                nameValuePairs.add(new BasicNameValuePair("login", passing[1]));
                nameValuePairs.add(new BasicNameValuePair("pass", passing[2]));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());

                DBHelper dbHelper = new DBHelper(Con);
                SQLiteDatabase dataBase = dbHelper.getWritableDatabase();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    Iterator<String> iter = jsonObject.keys();
                    while (iter.hasNext()) {
                        String keepName = iter.next();
                        try {
                            if (jsonObject.getJSONObject(keepName) != null) {
                                JSONObject keepData = jsonObject.getJSONObject(keepName);
                                ContentValues contentValues = new ContentValues();
                                dataBase.execSQL("drop table if exists " + keepName + TEXT_KEEP_ADD_KEY);
                                dataBase.execSQL("create table " + keepName + TEXT_KEEP_ADD_KEY + "(_id integer primary key, " + DBHelper.KEEP_TYPE_OF_DATA + " text,"
                                        + DBHelper.KEEP_VALUE_OF_DATA + " text," + DBHelper.KEEP_PARAMS_OF_DATA + " text)");

                                for (int i = 0; i < keepData.getJSONArray("table_keep_text").length(); i++) {
                                    String type = keepData.getJSONArray("table_keep_text").getJSONArray(i).getString(0);
                                    String value = keepData.getJSONArray("table_keep_text").getJSONArray(i).getString(1);
                                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, type);
                                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, value);
                                    dataBase.insert(keepName + TEXT_KEEP_ADD_KEY, null, contentValues);
                                }

                                try {
                                    Cursor tryCursor = dataBase.query(keepName + NOTIFICATION_KEEP_ADD_KEY, null, null, null, null, null, null);
                                    tryCursor.close();
                                } catch (Exception e) {
                                    dataBase.delete(DBHelper.TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{keepName});
                                    ContentValues contentKeepNameTable = new ContentValues();
                                    contentKeepNameTable.put(KEY_KEEP_NAME, keepName);
                                    String JSONParamsKeep = "";
                                    Calendar c = Calendar.getInstance();
                                    int dateYearRedact = c.get(Calendar.YEAR);
                                    int dateMonthRedact = c.get(Calendar.MONTH) + 1;
                                    int dateDayRedact = c.get(Calendar.DAY_OF_MONTH);
                                    int dateHourRedact = c.get(Calendar.HOUR_OF_DAY);
                                    int dateMinuteRedact = c.get(Calendar.MINUTE);
                                    JSONObject params = new JSONObject(keepData.getString("keep_params"));
                                    switch (params.getInt(DBHelper.JSON_KEY_COLOR_KEEP)) {
                                        case -1:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorWhiteKeep);
                                            break;
                                        case -2:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorRedKeep);
                                            break;
                                        case -3:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorOrangeKeep);
                                            break;
                                        case -4:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorYellowKeep);
                                            break;
                                        case -5:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorGreenKeep);
                                            break;
                                        case -6:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorLightBlueKeep);
                                            break;
                                        case -7:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorBlueKeep);
                                            break;
                                        case -8:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorFuchsiaKeep);
                                            break;
                                        default:
                                            params.put(DBHelper.JSON_KEY_COLOR_KEEP, R.color.colorWhiteKeep);
                                            break;
                                    }
                                    contentKeepNameTable.put(DBHelper.KEY_PARAMS_KEEP_NAME, String.valueOf(params));
                                    contentKeepNameTable.put(DBHelper.KEY_NEXT_EVENT_KEEP_NAME, Long.parseLong(keepData.getString("keep_next_event")));
                                    dataBase.insert(DBHelper.TABLE_KEEPS, null, contentKeepNameTable);

                                    dataBase.execSQL("drop table if exists " + keepName + NOTIFICATION_KEEP_ADD_KEY);
                                    dataBase.execSQL("create table " + keepName + NOTIFICATION_KEEP_ADD_KEY + "(_id integer primary key, " + DBHelper.KEEP_TYPE_OF_DATA + " text,"
                                            + DBHelper.KEEP_VALUE_OF_DATA + " text)");

                                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_IS_NOTIFY);
                                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, "false");
                                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_DATE_EVENT);
                                    JSONParamsKeep = "{"+DBHelper.JSON_KEY_DAY+":" + dateDayRedact + ", "+DBHelper.JSON_KEY_MONTH+":" + dateMonthRedact + ", "+DBHelper.JSON_KEY_YEAR+":" + dateYearRedact + "}";
                                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
                                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                                    contentValues.put(DBHelper.KEEP_TYPE_OF_DATA, DBHelper.TYPE_DATA_TIME_EVENT);
                                    JSONParamsKeep = "{"+DBHelper.JSON_KEY_HOUR+":" + dateHourRedact + ", "+DBHelper.JSON_KEY_MINUTE+":" + dateMinuteRedact + "}";
                                    contentValues.put(DBHelper.KEEP_VALUE_OF_DATA, JSONParamsKeep);
                                    dataBase.insert(keepName + NOTIFICATION_KEEP_ADD_KEY, null, contentValues);
                                }
                            } else {
                                dataBase.delete(DBHelper.TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{keepName});
                                dataBase.execSQL("drop table if exists " + keepName + TEXT_KEEP_ADD_KEY);
                                dataBase.execSQL("drop table if exists " + keepName + NOTIFICATION_KEEP_ADD_KEY);
                                dataBase.execSQL("drop table if exists " + keepName + SearchNextNotify.NEXT_NOTIFY_ADD_KEY);
                            }
                        } catch (JSONException e) {
                            dataBase.delete(DBHelper.TABLE_KEEPS, KEY_KEEP_NAME + " =?", new String[]{keepName});
                            dataBase.execSQL("drop table if exists " + keepName + TEXT_KEEP_ADD_KEY);
                            dataBase.execSQL("drop table if exists " + keepName + NOTIFICATION_KEEP_ADD_KEY);
                            dataBase.execSQL("drop table if exists " + keepName + SearchNextNotify.NEXT_NOTIFY_ADD_KEY);
                        }
                    }
                } catch (JSONException e){}

                dataBase.close();
            } else if(passing[0].equals("remindEmail")) {
                http = new HttpPost("https://web-umemo.ru/MailReminder.php");
                nameValuePairs.add(new BasicNameValuePair("email", passing[1]));
                String s = passing[2];
                nameValuePairs.add(new BasicNameValuePair("title", new String(s.getBytes("UTF-8"), "ISO-8859-1")));
                s = passing[3];
                nameValuePairs.add(new BasicNameValuePair("text", new String(s.getBytes("UTF-8"), "ISO-8859-1")));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());
            } else if(passing[0].equals("getParams")) {
                http = new HttpPost("https://web-umemo.ru/get_params.php");
                nameValuePairs.add(new BasicNameValuePair("login", passing[1]));
                nameValuePairs.add(new BasicNameValuePair("pass", passing[2]));
                http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = (String) httpclient.execute(http, new BasicResponseHandler());

                try {
                    JSONObject params = new JSONObject(response);

                    SharedPreferences sPref = Con.getSharedPreferences("com.service.istikers.alexander.istikers", Con.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("email", params.getString("email"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
    }
}
