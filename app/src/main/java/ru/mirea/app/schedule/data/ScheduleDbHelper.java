package ru.mirea.app.schedule.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Senik on 12.03.2015.
 */
public class ScheduleDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "schedule.db";

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_SCHEDULE = "CREATE TABLE " + ScheduleContract.ScheduleEntry.TABLE_NAME + " (" +

                ScheduleContract.ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ScheduleContract.ScheduleEntry.COLUMN_DAY_ID + " INTEGER NOT NULL, " +
                ScheduleContract.ScheduleEntry.COLUMN_CLASS_ID + " INTEGER NOT NULL, " +
                ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION + " INTEGER NOT NULL, " +
                ScheduleContract.ScheduleEntry.COLUMN_ROOM + " TEXT NOT NULL);";

        final String SQL_CLASS = "CREATE TABLE " + ScheduleContract.ClassEntry.TABLE_NAME + " (" +

                ScheduleContract.ClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ScheduleContract.ClassEntry.COLUMN_CLASS_NAME + " TEXT NOT NULL, " +
                ScheduleContract.ClassEntry.COLUMN_CLASS_ID + " INTEGER NOT NULL, " +
                ScheduleContract.ClassEntry.COLUMN_TEACHER + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_SCHEDULE);
        sqLiteDatabase.execSQL(SQL_CLASS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScheduleContract.ClassEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScheduleContract.ScheduleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
