package ru.mirea.app.schedule;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.mirea.app.schedule.data.ScheduleContract;
import ru.mirea.app.schedule.service.ScheduleUpdateService;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ScheduleAdapter scheduleAdapter;
    private String group;
    private ListView scheduleListView;
    public boolean useNowLayout = true;
    protected int position;
    private final int SCHEDULE_LOADER = 0;
    private final String SEL_KEY = "sel_key";

    private static final String[] SCHEDULE_COLUMNS = {
            ScheduleContract.ScheduleEntry.TABLE_NAME + "." + ScheduleContract.ScheduleEntry._ID,
            ScheduleContract.ScheduleEntry.COLUMN_DAY_ID,
            ScheduleContract.ClassEntry.COLUMN_CLASS_NAME,
            ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION,
            ScheduleContract.ScheduleEntry.COLUMN_ROOM
    };

    public static final int COLUMN_SCHEDULE_ID = 0;
    public static final int COLUMN_SCHEDULE_DAY_ID = 1;
    public static final int COLUMN_CLASS_NAME = 2;
    public static final int COLUMN_CLASS_POS = 3;
    public static final int COLUMN_ROOM = 4;

    public int pos;

    public ScheduleFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        refreshSchedule();
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String order = ScheduleContract.ScheduleEntry.COLUMN_CLASS_POSITION + " ASC";

        Uri scheduleUriForDay = ScheduleContract.ScheduleEntry.buildScheduleUri(Utility.getDayOfTheWeek());
        return new CursorLoader(
                getActivity(),
                scheduleUriForDay,
                SCHEDULE_COLUMNS,
                null,
                null,
                order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        scheduleAdapter.swapCursor(cursor);
        if (ListView.INVALID_POSITION != position) {
            scheduleListView.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        scheduleAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshSchedule() {
        Intent serviceIntent = new Intent(getActivity(), ScheduleUpdateService.class);
        serviceIntent.putExtra(ScheduleUpdateService.GROUP_NAME_KEY, Utility.getChosenGroup(getActivity()));
        getActivity().startService(serviceIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshSchedule();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_frag, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        scheduleAdapter = new ScheduleAdapter(getActivity(), null, 0);

        scheduleListView = (ListView) rootView.findViewById(R.id.schedule_listview);
        scheduleListView.setAdapter(scheduleAdapter);
        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(ScheduleContract.ScheduleEntry.buildClassUriForDay(
                                    cursor.getInt(COLUMN_SCHEDULE_DAY_ID),
                                    cursor.getInt(COLUMN_CLASS_POS)
                            ));
                    pos = position;
                }
            }
        });

        if (null != savedInstanceState && savedInstanceState.containsKey("selected_position")) {
            position = savedInstanceState.getInt("selected_position");
        }

        scheduleAdapter.setUseNowLayout(useNowLayout);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (position != ListView.INVALID_POSITION) {
            outState.putInt(SEL_KEY, position);
        }
        super.onSaveInstanceState(outState);
    }

    public void setUseNowLayout(boolean use) {
        useNowLayout = use;
        if (scheduleAdapter != null) scheduleAdapter.setUseNowLayout(use);
    }

    public interface ListCallback {
        public void onItemSelected(int classId);
    }

    public interface Callback {
        public void onItemSelected(Uri uri);
    }

}