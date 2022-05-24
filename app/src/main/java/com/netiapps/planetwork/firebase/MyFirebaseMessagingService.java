package com.netiapps.planetwork.firebase;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap bmp = null;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    String image="";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String message=remoteMessage.getNotification().getBody();
        String tittle=remoteMessage.getNotification().getTitle();
        image= String.valueOf(remoteMessage.getNotification().getImageUrl());
        Log.v("TAG","F-SMS"+image);

        try {
            bmp = Glide
                    .with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .submit()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bmp!=null) {
            FireNotification(message, tittle);
        }
        else
        {
            FireNotificationwithoutImage(message,tittle);
        }
    }

    @Override
    public void onCreate() {

        sharedPreferences=getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        super.onCreate();
    }

    private void FireNotification(String messsage, String tittle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.nt);  //Here is FILE_NAME is the name of file that you want to play

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(tittle)
                        .setContentText(messsage)
                         .setLargeIcon(bmp)
                         .setContentIntent(pendingIntent)
                         .setStyle(
                                 new NotificationCompat.BigPictureStyle().bigPicture(bmp))
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        else {
            notificationManager=
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(sound != null){
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(channelId,"pp",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(sound,audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Random r = new Random();
            int randomNumber = r.nextInt(10);
            notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());

        }
        else {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }

    private void FireNotificationwithoutImage(String messsage, String tittle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.nt);  //Here is FILE_NAME is the name of file that you want to play

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(tittle)
                        .setContentText(messsage)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        else {
            notificationManager=
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(sound != null){
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(channelId,"pp",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(sound,audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Random r = new Random();
            int randomNumber = r.nextInt(10);
            notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());

        }
        else {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        mEditor.putString("fcm_token",token);
        mEditor.apply();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        call_update_token_api(token);
    }

    private void call_update_token_api(String token) {
        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        //  listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        // listDetails.put(Constants.JOBID, jobId);
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("token", token);
        listDetails.put("model", Build.MODEL);

        //  listDetails.put("status", "Work On Hold");

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().updatefcmToken(mHeaders,listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                try {

                    String result= new Gson().toJson(response.body());
                    JSONObject jsonObject=new JSONObject(result);

                    String status=jsonObject.getString("status");
                    if(status.equalsIgnoreCase("1")){

                        Log.v("TAG","FCM Token updated"+token);
                        mEditor.putString("fcm_token",token);
                        mEditor.apply();

                    }
                    else {

                        Log.v("TAG","Failed to update FCM Token"+token);
                        mEditor.putString("fcm_token"," ");
                        mEditor.apply();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("TAG","Retrofit error "+t.getMessage());


            }
        });
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
