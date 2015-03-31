package ru.mirea.app.schedule.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Senik on 12.03.2015.
 */
public class ScheduleProvider extends ContentProvider {

    private ScheduleDbHelper scheduleDbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int SCHEDULE = 10;
    private static final int CLASS = 20;
    private static final int SCHEDULE_DAY = 11;
    private static final int SCHEDULE_DAY_CLASS = 12;
    private static final int CLASS_ID = 21;

    private static final SQLiteQueryBuilder queryBuilder;

    static {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(
                        ScheduleContract.ScheduleEntry.TABLE_NAME
                        + " LEFT OUTER JOIN " +
                        ScheduleContract.ClassEntry.TABLE_NAME +
                        " ON "
                        + ScheduleContract.ScheduleEntry.TABLE_NAME +
                        "."
                        + ScheduleContract.ScheduleEntry.COLUMN_CLASS_ID +
                        " = "
                        + ScheduleContract.ClassEntry.TABLE_NAME +
                        "."
                        + ScheduleContract.ClassEntry.COLUMN_CLASS_ID);
    }

    private static final String daySelection =
            ScheduleContract.ScheduleEntry.TABLE_NAME +
                    "." + ScheduleContract.ScheduleEntry.COLUMN_DAY_ID + " = ? ";

    private static final String dayClassSelection =
            ScheduleContract.ScheduleEntry.TABLE_NAME +
                    "." + ScheduleContract.ScheduleEntry.COLUMN_DAY_ID + " = ? AND " +
                    ScheduleContract.ScheduleEntry.TABLE_NAME + "." +
                    ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION + " = ? ";

    @Override
    public boolean onCreate() {
        scheduleDbHelper = new ScheduleDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Isn't in use at all
        return null;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            // "schedule/*/#"
            case SCHEDULE_DAY_CLASS:
            {
                cursor = getClassInfoForThatDay(uri, projection, sortOrder);
                break;
            }
            // "schedule/*"
            case SCHEDULE_DAY: {
                cursor = getScheduleForThatDay(uri, projection, sortOrder);
                break;
            }
            // "schedule"
            case SCHEDULE: {
                cursor = scheduleDbHelper.getReadableDatabase()
                        .query(
                                ScheduleContract.ScheduleEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            }
            // "class"
            case CLASS: {
                cursor = scheduleDbHelper.getReadableDatabase()
                        .query(
                                ScheduleContract.ClassEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
                break;
            }

            default:
                throw new UnsupportedOperationException("unsupported uri found " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getScheduleForThatDay(Uri uri, String[] projection, String sortOrder) {
        int dayId = ScheduleContract.getDayIDFromScheduleUri(uri);

        String[] selectionArgs;
        String selection;

        selection = daySelection;

        selectionArgs = new String[] {
            Integer.toString(dayId)
        };

        return queryBuilder.query(
                scheduleDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getClassInfoForThatDay(Uri uri, String[] projection, String sortOrder) {
        int dayId = ScheduleContract.getDayIDFromScheduleUri(uri);
        int classPos = ScheduleContract.getClassPosFromClassUri(uri);

        String[] selectionArgs;
        String selection;

        selection = dayClassSelection;

        selectionArgs = new String[] {
                Integer.toString(dayId),
                Integer.toString(classPos)
        };

        return queryBuilder.query(
                scheduleDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase writableDatabase = scheduleDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int deleted = 0;

        selection = selection == null ? "1" : null;

        switch (match) {
            case SCHEDULE: {
                deleted = writableDatabase.delete(ScheduleContract.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CLASS: {
                deleted = writableDatabase.delete(ScheduleContract.ClassEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }
        if (deleted != 0){
            getContext()
                    .getContentResolver()
                    .notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase writtalbeDatabase = scheduleDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int updated = 0;

        selection = selection == null ? "1" : null;

        switch (match) {
            case SCHEDULE: {
                updated = writtalbeDatabase.update(ScheduleContract.ScheduleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CLASS: {
                updated = writtalbeDatabase.update(ScheduleContract.ClassEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("unsupported uri found:  " + uri);
        }
        if (updated != 0){
            getContext()
                    .getContentResolver()
                    .notifyChange(uri, null);
        }

        return updated;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULE : return ScheduleContract.ScheduleEntry.CONTENT_TYPE;
            case SCHEDULE_DAY : return ScheduleContract.ScheduleEntry.CONTENT_ITEM_TYPE;
            case SCHEDULE_DAY_CLASS : return ScheduleContract.ScheduleEntry.CONTENT_ITEM_TYPE;
            case CLASS : return ScheduleContract.ClassEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("unsupported uri: " + uri);
        }
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULE, SCHEDULE);
        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULE + "/*", SCHEDULE_DAY);
        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULE + "/*/#", SCHEDULE_DAY_CLASS);
        matcher.addURI(authority, ScheduleContract.PATH_CLASS, CLASS);
        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = scheduleDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case SCHEDULE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CLASS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScheduleContract.ClassEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
