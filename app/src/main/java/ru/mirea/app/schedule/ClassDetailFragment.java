package ru.mirea.app.schedule;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.mirea.app.schedule.data.ScheduleContract;

public class ClassDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String URI_KEY = "URI";

    public TextView subjectView;
    public TextView durationView;
    public TextView roomView;
    public TextView teacherView;

    private static final String[] SCHEDULE_COLUMNS = {
            ScheduleContract.ScheduleEntry.TABLE_NAME + "." + ScheduleContract.ScheduleEntry._ID,
            ScheduleContract.ScheduleEntry.COLUMN_DAY_ID,
            ScheduleContract.ClassEntry.COLUMN_CLASS_NAME,
            ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION,
            ScheduleContract.ScheduleEntry.COLUMN_ROOM,
            ScheduleContract.ClassEntry.COLUMN_TEACHER,
    };

    public static final int COLUMN_SCHEDULE_ID = 0;
    public static final int COLUMN_SCHEDULE_DAY_ID = 1;
    public static final int COLUMN_CLASS_NAME = 2;
    public static final int COLUMN_CLASS_POS = 3;
    public static final int COLUMN_ROOM = 4;
    public static final int COLUMN_TEACHER= 5;

    public static final int DETAIL_LOADER = 1;

    private Uri uri;

    public ClassDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.fragment_class_detail, container, false);;

        if (bundle != null) {
            uri = bundle.getParcelable(ClassDetailFragment.URI_KEY);
        } else {
            if (savedInstanceState != null && savedInstanceState.containsKey(URI_KEY)) {
                uri = Uri.parse(savedInstanceState.getString(URI_KEY));
            } else {
                getLoaderManager().destroyLoader(DETAIL_LOADER);
                return null;
            }
        }

        subjectView = (TextView) view.findViewById(R.id.subject);
        durationView = (TextView) view.findViewById(R.id.duration);
        roomView = (TextView) view.findViewById(R.id.room);
        teacherView = (TextView) view.findViewById(R.id.teacher);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != uri) {

            return new CursorLoader(
                    getActivity(),
                    uri,
                    SCHEDULE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    public void refreshDetails() {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || !data.moveToFirst()) return;
        String className = data.getString(COLUMN_CLASS_NAME);
        String duration = Utility.getDurationForPos(data.getInt(COLUMN_CLASS_POS));
        String room = data.getString(COLUMN_ROOM);
        String teacher = data.getString(COLUMN_TEACHER);

        subjectView.setText(className);
        durationView.setText(duration);
        roomView.setText(room);
        teacherView.setText(teacher);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (uri != null) {
            outState.putString(URI_KEY, uri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onGroupChanged() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }
}
