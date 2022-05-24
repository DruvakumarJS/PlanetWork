package com.netiapps.planetwork.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class LocalHelper {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;


    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        return true;

                    }

        }

        return false;
    }

    public static void showNotification(Context context, String tittle , String message) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.nt);  //Here is FILE_NAME is the name of file that you want to play

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(tittle)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)

                        .setAutoCancel(true)
                        .setSound(sound, AudioManager.STREAM_NOTIFICATION)
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



        /*Intent intent = new Intent(context, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 *//* Request code *//*, intent,
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
                    "Vidwath App",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            Random r = new Random();
            int randomNumber = r.nextInt(10);
            notificationManager.notify(randomNumber *//* ID of notification *//*, notificationBuilder.build());

        }
         else {
             notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());

        }*/

    }

    public static void showbottomsheet(Context context, String message){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context );
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        bottomSheetDialog.setCancelable(false);

        TextView tvcaution=bottomSheetDialog.findViewById(R.id.tvcaustionview);
        Button btngotit=bottomSheetDialog.findViewById(R.id.btngot);

        tvcaution.setText(message);
        btngotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.show();

    }

    public static void showNotificationwithlatlong(Context context, String tittle , String message , String lat , String lang,String ismap) {
      /*  Intent intent = new Intent(context, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lang);
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        intent.putExtra("lat",lat);
        intent.putExtra("long",lang);
        intent.putExtra("ismap",ismap);

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
                    "Planetwork",
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

    public static void call_attendednce_api(String type,Context context) {
        SharedPreferences sharedPreferences = (SharedPreferences) context.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();

        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("type",type);
        listDetails.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(type.equalsIgnoreCase("logout")) {
            listDetails.put("login_id", sharedPreferences.getString("attendence_login_id", ""));
        }
        else {
            listDetails.put("login_id", "0");
        }

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().attendence(mHeaders, listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {

                     String result = new Gson().toJson(response.body());
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        if(type.equalsIgnoreCase("login")) {
                            String loginID = jsonObject.getString("login_id");
                            mEditor.putString("attendence_login_id", loginID);
                            mEditor.apply();
                            Toast.makeText(context, "LOGIN", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mEditor.putString("attendence_login_id", "0");
                            mEditor.apply();
                            Toast.makeText(context, "LOGOUT", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("TAG", "Retrofit error " + t.getMessage());
            }
        });

    }

}
