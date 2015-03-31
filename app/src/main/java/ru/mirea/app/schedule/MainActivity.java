package ru.mirea.app.schedule;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements ScheduleFragment.Callback {

    public boolean tabletMode = false;
    public String group;
    public String SFTAG = "sftag";
    public String DFTAG = "dftag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.group = Utility.getChosenGroup(getApplicationContext());
        if (findViewById(R.id.class_detail_container) != null) {
            tabletMode = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.class_detail_container, new ClassDetailFragment(), DFTAG)
                    .commit();
            ScheduleFragment sf = (ScheduleFragment)(getSupportFragmentManager().findFragmentById(R.id.schedule_fragment));
            sf.setUseNowLayout(false);
        } else {
            tabletMode = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String group = Utility.getChosenGroup(this);
        if (group != null && !group.equals(this.group)) {
            ScheduleFragment sf = (ScheduleFragment)getSupportFragmentManager().findFragmentByTag(SFTAG);
            if ( null != sf ) {
                sf.refreshSchedule();
            }
            ClassDetailFragment df = (ClassDetailFragment)getSupportFragmentManager().findFragmentByTag(DFTAG);
            if ( null != df ) {
                df.refreshDetails();
            }
            this.group = group;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (tabletMode) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ClassDetailFragment.URI_KEY, contentUri);
            ClassDetailFragment fragment = new ClassDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.class_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ClassDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

}
