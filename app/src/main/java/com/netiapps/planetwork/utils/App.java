package com.netiapps.planetwork.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.netiapps.planetwork.services.RunApplicationInBackground;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
/*

        Intent intent=new Intent(this,RunApplicationInBackground.class);

        ContextCompat.startForegroundService(getApplicationContext(), intent);
*/

        startService(new Intent(this, RunApplicationInBackground.class));
    }
}
