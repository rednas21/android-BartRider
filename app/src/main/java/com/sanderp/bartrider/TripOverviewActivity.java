package com.sanderp.bartrider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sanderp.bartrider.adapters.TripAdapter;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.QuickPlannerAsyncTask;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.Trip;

import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnConfirmListener {
    private static final String TAG = "TripOverviewActivity";

    private static final String PREFS_NAME = "BartRiderPrefs";
    private static final String FIRST_RUN = "first_Run";

    private FragmentManager fragmentManager;
    private TripPlannerFragment fragment;
    private List<Trip> trips;
    private SharedPreferences prefs;

    private FloatingActionButton mFab;
    private ListView mListView;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        prefs = getSharedPreferences(PREFS_NAME, 0);

        // Initialize list view
        mListView = (ListView) findViewById(R.id.trip_list_view);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Trip selectedTrip = trips.get(position);
                 Intent tripDetailIntent = new Intent(TripOverviewActivity.this, TripDetailActivity.class);
                 tripDetailIntent.putExtra("trip", selectedTrip);
                 startActivity(tripDetailIntent);
             }
         });

        if (prefs.getBoolean(FIRST_RUN, true)) {
            // Populate the database with station info
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    prefs.edit().putBoolean(FIRST_RUN, false).commit();
                }
            }, this).execute();
        }

        fragmentManager = getSupportFragmentManager();
        fragment = (TripPlannerFragment) fragmentManager.findFragmentById(R.id.trip_planner_fragment);
        hideFragment();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bart_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                updateListItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListItems() {
        final Spinner o = (Spinner) fragment.getView().findViewById(R.id.orig_spinner);
        final Spinner d = (Spinner) fragment.getView().findViewById(R.id.dest_spinner);
        String origin = getAbbreviation((Cursor) o.getSelectedItem());
        String destination = getAbbreviation((Cursor) d.getSelectedItem());
        if (!origin.equals(destination)) {
            new QuickPlannerAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    trips = (List<Trip>) result;
                    for (Trip t : trips) {
                        t.setOrigFullName(getName((Cursor) o.getSelectedItem()));
                        t.setDestFullName(getName((Cursor) d.getSelectedItem()));
                    }
                    TripAdapter adapter = new TripAdapter(TripOverviewActivity.this, trips);
                    mListView.setAdapter(adapter);
                }
            }).execute(origin, destination);
        }
    }

    private void hideFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    private void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    private String getName(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.NAME));
    }

    private String getAbbreviation(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.ABBREVIATION));
    }

    @Override
    public void onConfirm() {
        updateListItems();
    }
}
