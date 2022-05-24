package com.netiapps.planetwork.geofencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitions";
    Context context;

    public GeofenceTransitionsIntentService() {
        //super("GeofenceTransitionsIntentService");
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.v(TAG,"Service called ");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "onHandleIntent");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
            //      geofencingEvent.getErrorCode());
            Log.e(TAG, "Goefencing Error " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        Log.i(TAG, "geofenceTransition = " + geofenceTransition + " Enter : " + Geofence.GEOFENCE_TRANSITION_ENTER + "Exit : " + Geofence.GEOFENCE_TRANSITION_EXIT);
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            Toast.makeText(context, "YES", Toast.LENGTH_SHORT).show();
            showNotification("Entered the Location");
            showNotification("Entered");
        }
        else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Toast.makeText(context, "OUT", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Showing Notification...");
            showNotification("Exited the Location");
        } else {
            // Log the error.
            showNotification("Error");
            Toast.makeText(context, "NO", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Geofence error " + geofenceTransition);
            Log.e(TAG, "Error ");
        }
    }

    public void showNotification(String text) {

      /*  // 1. Create a NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 2. Create a PendingIntent for AllGeofencesActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 3. Create and send a notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText(text)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);*/

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setColor(Color.RED)
                .setContentTitle(text)
                .setContentText((text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "not Available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many Geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many Pending Intents";
            default:
                return "unknown geofence error";
        }
    }
}
