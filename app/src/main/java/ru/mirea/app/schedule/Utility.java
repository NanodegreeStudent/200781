package ru.mirea.app.schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.GregorianCalendar;

/**
 * Created by Senik on 13.03.2015.
 */
public class Utility {

    private static GregorianCalendar calendar = new GregorianCalendar();
    static {
        calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
    }

    public static int getClassPosNow() {
        int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = calendar.get(GregorianCalendar.MINUTE);
        int pos = 0;

        if (isInto(hour, 9, 10) && isInto(minute, 0, 35)) pos = 1;
        if (isInto(hour, 10, 12) && (isInto(minute, 45, 59) || isInto(minute, 0, 20))) pos = 2;
        if (isInto(hour, 12, 14) && (isInto(minute, 50, 59) || isInto(minute, 0, 25))) pos = 3;
        if (isInto(hour, 14, 16) && (isInto(minute, 35, 59) || isInto(minute, 0, 10))) pos = 4;
        if (isInto(hour, 16, 17) && (isInto(minute, 20, 59) || isInto(minute, 0, 55))) pos = 5;
        if (isInto(hour, 18, 21) && (isInto(minute, 0, 59) || isInto(minute, 0, 20))) pos = 6;
        return pos;
    }

    public static boolean isInto(int x, int start, int end) {
        if ((x >= start) && (x <= end)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDurationForPos(int pos) {
        switch (pos) {
            case 1: { return "9:00 - 10:35"; }
            case 2: { return "10:45 - 12:20";  }
            case 3: { return "12:50 - 14:25";  }
            case 4: { return "14:35 - 16:10"; }
            case 5: { return "16:20 - 17:55";  }
            case 6: { return "18:00 - 21:20";  }
        }
        return "9:00 - 10:35";
    }

    public static int getDayOfTheWeek() {
        int calendarPos = calendar.get(GregorianCalendar.DAY_OF_WEEK);
        //Fix 'invalid' dayofweek
        switch (calendarPos) {
            case GregorianCalendar.SUNDAY : return 7;
            case GregorianCalendar.MONDAY : return 1;
            case GregorianCalendar.TUESDAY : return 2;
            case GregorianCalendar.WEDNESDAY : return 3;
            case GregorianCalendar.THURSDAY : return 4;
            case GregorianCalendar.FRIDAY : return 5;
            case GregorianCalendar.SATURDAY : return 6;
        }
        return 1;
    }

    public static String getChosenGroup(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_group_key),
                context.getString(R.string.pref_group_default));
    }
}
