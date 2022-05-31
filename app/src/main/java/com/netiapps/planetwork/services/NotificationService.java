package com.netiapps.planetwork.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.LocalHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NotificationService extends Service {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    Handler handler = new Handler();

    public static final String COUNTDOWN_BR = "com.netiapps.planetwork.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    //static long TIME_LIMIT = 9*60*60*1000;
    static long TIME_LIMIT = 60*1000;
    CountDownTimer Count;
    String curent_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        Count = new CountDownTimer(TIME_LIMIT, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String time = String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60));

                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown",time);

               // Log.v("TAG","Service time "+time);

                sendBroadcast(i);
                //coundownTimer.setTitle(millisUntilFinished / 1000);

            }

            public void onFinish() {
                //coundownTimer.setTitle("Sedned!");
              //  Log.v("TAG","Service time completed ");
                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown","Sent!");

                sendBroadcast(i);
                mEditor.putString("session_loggedin","0");
                mEditor.putString("timer_paused","false");
                mEditor.putString("ot_started","false");
                mEditor.putString("time_at_destroy",curent_time);
                mEditor.putInt("total_seconds", 0);
                mEditor.apply();

                Log.d("session","session service :"+sharedPreferences.getString("session_loggedin",""));
                showNotification(getApplicationContext(), "Logged out", "Since , you haven't choose any task , you are logged out ");

                stopSelf();

            }
        };

        Count.start();
        return Service.START_STICKY;
    }

    public static void showNotification(Context context, String tittle , String message) {

     //   Log.v("TAG","Notification sent ");
        Intent intent = new Intent(context, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)

                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(tittle)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        else {
            notificationManager=
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "PlanetWork",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            Random r = new Random();
            int randomNumber = r.nextInt(10);
            notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());

        }
        else {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }

    }

    @Override
    public void onCreate() {
        sharedPreferences = (SharedPreferences) this.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        super.onCreate();
    }


}