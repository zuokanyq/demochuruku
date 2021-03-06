package com.example.timingsystem.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.timingsystem.Constants;
import com.example.timingsystem.receiver.AlarmReceiver;

public class LongRunningService extends Service {

    private Context mContext = this;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputIntentService.startActionPushInput(mContext);
                InputIntentService.startActionPushOutput(mContext);
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //int anHour = 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + Constants.CYCLE_TIME;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

}
