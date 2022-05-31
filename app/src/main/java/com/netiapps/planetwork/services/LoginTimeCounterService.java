package com.netiapps.planetwork.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.database.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LoginTimeCounterService extends Service {
    public static final String TIME_INFO = "time_info";

    private int count;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    private Handler handler;
    private  TimerTask timerTask;
    Timer t;
    TimerTask tt;
    int logouthour=9*60*60;
   // int logouthour=5*60;

    @Override
    public void onCreate() {
        Log.v("TAG","server called");
        sharedPreferences = getApplicationContext().getSharedPreferences("SharedPreferences_Key", MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        count=0;
        mEditor.putBoolean("session_end",false);
        mEditor.commit();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String request=intent.getStringExtra("request");

      /*  timer = new CounterClass(90000, 1000);
        timer.start();*/

       /* new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                startTimercount();
            }
        }, 0, 1000);*/

         t = new Timer();
         tt = new TimerTask() {
            @Override
            public void run() {
                startTimercount();
            };
        };
        t.schedule(tt,1000,1000);


        startRunningInForeground();

        return START_NOT_STICKY;
    }

    private void startTimercount() {

        Log.v("TAG","pased"+sharedPreferences.getBoolean("ispause",false));
        if(!sharedPreferences.getBoolean("ispause",false)) {

            count++;
            mEditor.putInt("service_time",count);
            mEditor.commit();
        }
        if(count>=logouthour)
        {
            String time =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String loginID=sharedPreferences.getString("DBloginID","");
            AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());
            db.logintaskDao().update(time,loginID);
            Log.i("DB", "Updated User Logout Data : "+loginID);
            mEditor.putString("DBloginID", "0");

            mEditor.putString("timer_paused","false");
            mEditor.putString("session_loggedin","0");
            mEditor.putBoolean("session_end",true);
            mEditor.putInt("service_time",0);
            mEditor.commit();
            onDestroy();
        }

        Log.d("DB","counter : "+count);

        Intent timerInfoIntent = new Intent(TIME_INFO);
        timerInfoIntent.putExtra("VALUE", String.valueOf(count));
        LocalBroadcastManager.getInstance(LoginTimeCounterService.this).sendBroadcast(timerInfoIntent);
    }

    private void startRunningInForeground() {
        if (Build.VERSION.SDK_INT >= 26) {

            if (Build.VERSION.SDK_INT > 26) {
                String CHANNEL_ONE_ID = "Package.Service";
                String CHANNEL_ONE_NAME = "Screen service";
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(notificationChannel);
                }

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setChannelId(CHANNEL_ONE_ID)
                        .setSmallIcon(R.drawable.appicon)
                        .build();

                Intent notificationIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                startForeground(101, notification);
            } else {
                startForeground(101, updateNotification());
            }
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.appicon)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
    }

    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DashBoardActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    @Override
    public void onDestroy() {
        Log.v("TAG","Ondestroy called");
        t.cancel();
        tt.cancel();
        stopSelf();
        super.onDestroy();
        Intent timerInfoIntent = new Intent(TIME_INFO);
        timerInfoIntent.putExtra("VALUE", "Stopped");
        LocalBroadcastManager.getInstance(LoginTimeCounterService.this).sendBroadcast(timerInfoIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}


