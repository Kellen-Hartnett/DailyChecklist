package com.my.kellen.dailychecklist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DataAdapter.ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private DataAdapter mAdapter;
    private SQLiteDatabase mDb;
    private String today;
    private SharedPreferences sP;
    private RecyclerView rView;
    private TextView doneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        sP = PreferenceManager.getDefaultSharedPreferences(this);

        rView = findViewById(R.id.rView);
        doneText = findViewById(R.id.textView);

        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        today = sdf.format(now.getTime());

        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent("com.my.kellen.databasetest.AlarmReceiver"),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Alarm alarm = new Alarm(getApplicationContext());
            alarm.scheduleAlarm();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(layoutManager);
        DbHelper dbHelper = new DbHelper(this);
        mDb = dbHelper.getWritableDatabase();


        mAdapter = new DataAdapter(this, getCursor(), this);
        rView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                if (direction == ItemTouchHelper.LEFT) {
                    markAsDone(id, 0);
                } else {
                    markAsDone(id, 1);
                }
            }
        }).attachToRecyclerView(rView);

        updateList();

    }

    private Cursor getFromDay() {
        return mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                Contract.Entry.COLUMN_DATE + "='" + today + "'" + " AND " + Contract.Entry.COLUMN_COMPLETE + "=" + "0",
                null,
                null,
                null,
                Contract.Entry._ID
        );
    }

    private Cursor getAllFromDay() {
        return mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                Contract.Entry.COLUMN_DATE + "='" + today + "'",
                null,
                null,
                null,
                Contract.Entry._ID
        );
    }

    private void markAsDone(long id, int done) {
        Cursor cursor = mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                Contract.Entry._ID + "=" + String.valueOf(id),
                null,
                null,
                null,
                Contract.Entry._ID
        );
        cursor.moveToPosition(0);
        if (cursor.getInt(4) == 0) {
            mDb.delete(Contract.Entry.TABLE_NAME, Contract.Entry._ID + "=" + String.valueOf(id), null);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(Contract.Entry.COLUMN_COMPLETE, done);
            mDb.update(Contract.Entry.TABLE_NAME, cv, Contract.Entry._ID + "=" + String.valueOf(id), null);
        }
        cursor.close();
        updateList();
    }

    private Cursor getCursor() {
        QueryMaker queryMaker = new QueryMaker(today, sP.getBoolean(getString(R.string.eventDisplayKey), false), sP.getBoolean(getString(R.string.showCompleteKey), false));
        String queryString = queryMaker.makeQuery();
        return mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                queryString,
                null,
                null,
                null,
                Contract.Entry._ID
        );

    }

    private void updateList() {
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        rView.setVisibility(View.VISIBLE);
        doneText.setVisibility(View.GONE);
        if (getFromDay().getCount() == 0) {
            if (mNotifyMgr != null) {
                mNotifyMgr.cancel(1);
            }
            if (!sP.getBoolean(getString(R.string.showCompleteKey), true)) {
                if (!sP.getBoolean(getString(R.string.showCompleteKey), true)) {
                    rView.setVisibility(View.GONE);
                    doneText.setVisibility(View.VISIBLE);
                } else if (getAllFromDay().getCount() == 0) {
                    rView.setVisibility(View.GONE);
                    doneText.setVisibility(View.VISIBLE);
                }
            }
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "1")
                            .setSmallIcon(R.drawable.ic_watch_later_white_24px)
                            .setContentTitle("Checklist")
                            .setContentText("Completed tasks for today: (" + String.valueOf(getAllFromDay().getCount() - getFromDay().getCount()) + "/" + String.valueOf(getAllFromDay().getCount()) + ")")
                            .setProgress(getAllFromDay().getCount(), getAllFromDay().getCount() - getFromDay().getCount(), false)
                            .setOngoing(true);

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(1, mBuilder.build());
            }


        }
        mAdapter.swapCursor(getCursor());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListClick(int clickedItemIndex) {
        Intent i = new Intent(this, CreateActivity.class);
        Cursor c = getCursor();
        c.moveToPosition(clickedItemIndex);
        i.putExtra("Name", c.getString(c.getColumnIndex(Contract.Entry.COLUMN_NAME)));
        i.putExtra("Date", c.getString(c.getColumnIndex(Contract.Entry.COLUMN_DATE)));
        i.putExtra("Repeat", c.getInt(c.getColumnIndex(Contract.Entry.COLUMN_REPEAT)));
        i.putExtra("Id", c.getLong(c.getColumnIndex(Contract.Entry._ID)));
        c.close();
        startActivity(i);
    }

    public void onClick(View view) {
        Intent i = new Intent(this, CreateActivity.class);
        startActivity(i);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //if (s.equals(getString(R.string.eventDisplayKey))){
        //}
        updateList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        mDb.close();
    }
}
