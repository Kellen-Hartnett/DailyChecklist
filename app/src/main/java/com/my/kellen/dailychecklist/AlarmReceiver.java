package com.my.kellen.dailychecklist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    private SQLiteDatabase mDb;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")
                || Objects.equals(intent.getAction(), "android.intent.action.QUICKBOOT_POWERON")
                || Objects.equals(intent.getAction(), "com.htc.intent.action.QUICKBOOT_POWERON")){
            boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), 1, new Intent("com.my.kellen.databasetest.AlarmReceiver"),
                    PendingIntent.FLAG_NO_CREATE) == null);

            if (alarmUp) {
                Alarm alarm = new Alarm(context.getApplicationContext());
                alarm.scheduleAlarm();
            }}
        DbHelper dbHelper = new DbHelper(context);
        mDb = dbHelper.getWritableDatabase();
        mDb.delete(Contract.Entry.TABLE_NAME, Contract.Entry.COLUMN_COMPLETE + "=" + "1" + " AND " + Contract.Entry.COLUMN_REPEAT + "=" + "0", null);
        Cursor c = mDb.query(Contract.Entry.TABLE_NAME, null, Contract.Entry.COLUMN_REPEAT + "=" + 1, null, null, null, Contract.Entry._ID);
        for (int i = 0; i <c.getCount() ; i++) {
            c.moveToPosition(i);
            long id = c.getLong(c.getColumnIndex(Contract.Entry._ID));
            ContentValues cv = new ContentValues();
            cv.put(Contract.Entry.COLUMN_COMPLETE, 0);
            mDb.update(Contract.Entry.TABLE_NAME, cv, Contract.Entry._ID + "=" + String.valueOf(id), null);
        }
        c.close();
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        String today = sdf.format(now.getTime());

        if (getFromDay(today).getCount()>0){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "1")
                        .setSmallIcon(R.drawable.ic_watch_later_white_24px)
                        .setContentTitle("Checklist")
                        .setContentText("Completed tasks for today: (0/" + String.valueOf(getAllFromDay(today).getCount()) + ")")
                        .setProgress(getAllFromDay(today).getCount(), 0, false)
                .setOngoing(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(1, mBuilder.build());
        }}
    }

    private Cursor getFromDay(String day) {
        return mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                Contract.Entry.COLUMN_DATE + "='" + day + "'" + " AND " + Contract.Entry.COLUMN_COMPLETE + "=" + "0",
                null,
                null,
                null,
                Contract.Entry._ID
        );
}

    private Cursor getAllFromDay(String day) {
        return mDb.query(
                Contract.Entry.TABLE_NAME,
                null,
                Contract.Entry.COLUMN_DATE + "='" + day + "'",
                null,
                null,
                null,
                Contract.Entry._ID
        );
    }}

