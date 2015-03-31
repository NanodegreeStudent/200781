package ru.mirea.app.schedule.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Senik on 12.03.2015.
 */
public class ScheduleContract {

    public static final String CONTENT_AUTHORITY = "ru.mirea.app.schedule";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SCHEDULE = "schedule";
    public static final String PATH_CLASS = "class";

    public static final class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_DAY_ID = "dayId";
        public static final String COLUMN_CLASS_ID = "classId";
        public static final String COLUMN_CLASS_POSITION = "classPos";
        public static final String COLUMN_ROOM = "room";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        public static Uri buildScheduleUri(long id) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            return uri;
        }

        public static Uri buildClassUriForDay(int dayId, int classPos) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(Integer.toString(dayId)).build();
            uri = ContentUris.withAppendedId(uri, classPos);
            return uri;
        }
    }
    public static final class ClassEntry implements BaseColumns {
        public static final String TABLE_NAME = "class";
        public static final String COLUMN_CLASS_ID = "classId";
        public static final String COLUMN_CLASS_NAME = "class_name";
        public static final String COLUMN_TEACHER = "teacher";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASS;

        public static Uri buildClassUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static int getDayIDFromScheduleUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }

    public static int getClassPosFromClassUri(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(2));
    }

}
