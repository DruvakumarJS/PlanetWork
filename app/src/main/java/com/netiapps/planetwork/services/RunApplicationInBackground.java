package com.netiapps.planetwork.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;

public class RunApplicationInBackground extends Service {

    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.package.MyService";
    public static final String NOTIFICATION_CHANNEL_ID_INFO = "com.package.download_info";

    @Override
    public void onCreate() {
        super.onCreate();
       // startForeground();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "Download Info", NotificationManager.IMPORTANCE_DEFAULT));
        } else {
            Notification notification = new Notification();
            startForeground(1, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here

        return super.onStartCommand(intent, flags, startId);
    }


}
