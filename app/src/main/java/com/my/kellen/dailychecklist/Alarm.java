package com.my.kellen.dailychecklist;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Calendar;

class Alarm {
    private final Context mContext;

    Alarm(Context pContext) {
        mContext = pContext;
    }

    void scheduleAlarm() {
        IntentFilter filter = new IntentFilter("com.my.kellen.databasetest.AlarmReceiver");
        AlarmReceiver myReceiver = new AlarmReceiver();
        mContext.registerReceiver(myReceiver, filter);
        Intent intentAlarm = new Intent();
        intentAlarm.setAction("com.my.kellen.databasetest.AlarmReceiver");

        // create the object
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intentAlarm, 0);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 24);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
        Log.i("Alarm", "Scheduled");
    }

}
