package ru.mirea.app.schedule;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Senik on 12.03.2015.
 */
public class ScheduleAdapter extends android.support.v4.widget.CursorAdapter {

    public boolean useNowLayout = true;

    public int VIEW_TYPES = 2;
    public int VIEW_TYPE_DEF = 0;
    public int VIEW_TYPE_CURRENT = 1;

    // Running smoothly
    public static class ListItemViewHolder {
        public final TextView subjectView;
        public final TextView durationView;
        public final TextView roomView;
        public final TextView posView;

        public ListItemViewHolder(View v) {
            subjectView = (TextView) v.findViewById(R.id.list_item_subject);
            durationView = (TextView) v.findViewById(R.id.list_item_duration);
            roomView = (TextView) v.findViewById(R.id.list_item_room);
            posView = (TextView) v.findViewById(R.id.list_item_pos);
        }
    }

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ListItemViewHolder viewHolder = (ListItemViewHolder) view.getTag();

        String className = cursor.getString(ScheduleFragment.COLUMN_CLASS_NAME);
        String classPos = Integer.toString(cursor.getInt(ScheduleFragment.COLUMN_CLASS_POS));
        String duration = Utility.getDurationForPos(cursor.getInt(ScheduleFragment.COLUMN_CLASS_POS));
        String room = cursor.getString(ScheduleFragment.COLUMN_ROOM);

        viewHolder.subjectView.setText(className);
        viewHolder.durationView.setText(duration);
        viewHolder.roomView.setText(room);
        viewHolder.posView.setText(classPos);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewtype = getItemViewType(cursor.getInt(ScheduleFragment.COLUMN_CLASS_POS));
        int layout = -1;
        layout = viewtype == VIEW_TYPE_CURRENT ? R.layout.schedule_listview_item_current : R.layout.schedule_listview_item;
        View view = LayoutInflater
                .from(context)
                .inflate(layout, parent, false);
        ListItemViewHolder viewHolder = new ListItemViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    public void setUseNowLayout(boolean use) {
        useNowLayout = use;
    }

    @Override
    public int getItemViewType(int classPos) {
        return (classPos == Utility.getClassPosNow() && useNowLayout && Utility.getClassPosNow() != 0) ? VIEW_TYPE_CURRENT : VIEW_TYPE_DEF;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPES;
    }
}
