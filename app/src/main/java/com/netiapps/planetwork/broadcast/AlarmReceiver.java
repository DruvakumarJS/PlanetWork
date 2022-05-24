package com.netiapps.planetwork.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //intent = new Intent(context, MyService.class);

      // ContextCompat.startForegroundService(context,intent);
       // listener.doSomething("logout");
    }
}
