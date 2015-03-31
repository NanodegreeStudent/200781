package ru.mirea.app.schedule.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import ru.mirea.app.schedule.data.ScheduleContract;

/**
 * Created by Senik on 13.03.2015.
 */
public class ScheduleUpdateService extends IntentService {
    private ArrayAdapter<String> scheduleAdapter;
    private Context context;
    private final String TAG = ScheduleUpdateService.class.getSimpleName();
    public static final String GROUP_NAME_KEY = "group_name";

    public ScheduleUpdateService() {
        super("Schedule");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String group = intent.getStringExtra(GROUP_NAME_KEY);
        final String BASE_URL = "http://zombieplace.ru/schedule.php?";
        final String GROUP_PARAMETER = "group";
        //Get rawString with schedule in json format
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String rawString = null;
        try {
            Uri uri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(GROUP_PARAMETER, group).build();
            URL url = new URL(uri.toString());

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) return;

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String rawline = null;
            while ((rawline = bufferedReader.readLine()) != null) stringBuffer.append(rawline);

            if (stringBuffer.length() == 0) {
                return;
            } else {
                rawString = stringBuffer.toString();
            }

        } catch (IOException ioex) {
            Log.e(TAG, "error info: ", ioex);
            return;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "close stream error: ", e);
                }
            }
        }

        //Get schedule and add it to DB

        final String JSON_CLASSES = "classes";
        final String JSON_SCHEDULE = "schedule";
        final String JSON_CLASSID = "classId";
        final String JSON_CLASSPOS = "classPos";
        final String JSON_ROOM = "room";
        final String JSON_TEACHER = "teacher";
        final String JSON_CLASS_NAME = "class_name";

        try {
            JSONObject scheduleJson = new JSONObject(rawString);
            JSONArray daysArray = scheduleJson.getJSONArray(JSON_SCHEDULE);
            JSONArray classesArray = scheduleJson.getJSONArray(JSON_CLASSES);
            Vector<ContentValues> daysVector = new Vector<ContentValues>(daysArray.length());
            Vector<ContentValues> classesVector = new Vector<ContentValues>(classesArray.length());
            JSONArray day;
            JSONObject subject;
            int classId;
            int classPos;
            String room;
            String teacher;
            String className;
            ContentValues scheduleValues;
            ContentValues subjectValues;


            for (int i = 0; i < daysArray.length(); i++) {
                //days
                int q = 0;
                if (!daysArray.isNull(i)) {
                    day = daysArray.getJSONArray(i);
                    for (q = 0; q < day.length(); q++) {
                        //classes
                        if (!day.isNull(q)) {
                            subject = day.getJSONObject(q);
                            classId = subject.getInt(JSON_CLASSID);
                            classPos = subject.getInt(JSON_CLASSPOS);
                            room = subject.getString(JSON_ROOM);

                            scheduleValues = new ContentValues();
                            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_DAY_ID, i+1);
                            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION, classPos);
                            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_CLASS_ID, classId);
                            scheduleValues.put(ScheduleContract.ScheduleEntry.COLUMN_ROOM, room);

                            daysVector.add(scheduleValues);

                        }
                    }
                }
            }

            //clear
            subject = null;

            for (int m = 0; m < classesArray.length(); m++) {
                if (!classesArray.isNull(m)) {
                    subject = classesArray.getJSONObject(m);
                    subjectValues = new ContentValues();

                    teacher = subject.getString(JSON_TEACHER);
                    className = subject.getString(JSON_CLASS_NAME);

                    // id autoincrement <=> id in schedule teacher list
                    subjectValues.put(ScheduleContract.ClassEntry.COLUMN_TEACHER, teacher);
                    subjectValues.put(ScheduleContract.ClassEntry.COLUMN_CLASS_NAME, className);
                    subjectValues.put(ScheduleContract.ClassEntry.COLUMN_CLASS_ID, m);

                    classesVector.add(subjectValues);
                }
            }

            if (classesVector.size() != 0) {
                ContentValues[] cvarray = new ContentValues[classesVector.size()];
                classesVector.toArray(cvarray);
                this.getContentResolver().delete(ScheduleContract.ClassEntry.CONTENT_URI, null, null);
                this.getContentResolver().bulkInsert(ScheduleContract.ClassEntry.CONTENT_URI, cvarray);
            }

            if (daysVector.size() != 0) {
                ContentValues[] cvarray2 = new ContentValues[daysVector.size()];
                daysVector.toArray(cvarray2);
                this.getContentResolver().delete(ScheduleContract.ScheduleEntry.CONTENT_URI, null, null);
                this.getContentResolver().bulkInsert(ScheduleContract.ScheduleEntry.CONTENT_URI, cvarray2);
            }
            Log.d(TAG, "schedule service completed");

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
