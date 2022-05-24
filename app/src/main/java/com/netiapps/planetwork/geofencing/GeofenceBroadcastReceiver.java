package com.netiapps.planetwork.geofencing;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.TaskDetails;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReciever";
    Context mcontext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;

    // ...
    public void onReceive(Context context, Intent intent) {
        mcontext=context;
        sharedPreferences = (SharedPreferences) context.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    context,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails);

            Toast.makeText(mcontext, ""+geofenceTransitionDetails, Toast.LENGTH_SHORT).show();

            Vibrator v;
            v=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);

            Toast.makeText(mcontext, "insidegeofencing "+sharedPreferences.getBoolean("insideGeofence",false), Toast.LENGTH_SHORT).show();
           // sendNotification("insidegeofencing "+sharedPreferences.getBoolean("insideGeofence",false));
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, "jjj"+
                    geofenceTransition);

        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                mEditor.putBoolean("insideGeofence",true);
                mEditor.putString("geofencetaskid",sharedPreferences.getString("job_id","0"));
                mEditor.commit();

                return "transition - enter";

               case Geofence.GEOFENCE_TRANSITION_EXIT:
                mEditor.putBoolean("insideGeofence",false);
                mEditor.commit();
                return "transition - exit";

            default:
                mEditor.putBoolean("insideGeofence",false);
                mEditor.commit();
                return "unknown transition";
        }
    }



    private void sendNotification(String notificationDetails) {
        Log.v(TAG,"sending Notification");

        // Create an explicit content Intent that starts the main Activity.
        Intent intent = new Intent(mcontext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "10001";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mcontext, channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle("Planetwork Geofencing")
                        .setContentText(notificationDetails)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        else {
            notificationManager=
                    (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
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


}