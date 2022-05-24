package com.netiapps.planetwork.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class OfflineLocationServiceBackup extends Service {

    private final IBinder binder = new LocalBinder();
    LocationManager locationManager;
    Context context;

    SharedPreferences pending_job_pref;
    SharedPreferences.Editor mEditor;
    String user_id;


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        pending_job_pref = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = pending_job_pref.edit();
        user_id = pending_job_pref.getString("1", "");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread thread = new Thread(new Mythreadclass());
        thread.start();

        if (Build.VERSION.SDK_INT > 25) {
            startRunningInForeground();
        }
        return Service.START_STICKY;
    }

    public void setCallbacks(DashBoardActivity dashBoardActivity) {
    }


    public class LocalBinder extends Binder {
        public OfflineLocationServiceBackup getService() {
            // Return this instance of LocalService so clients can call public methods
            return OfflineLocationServiceBackup.this;
        }
    }

    class Mythreadclass implements Runnable {

        @Override
        public void run() {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    //here show dialog
                    getloactionupdate();
                }
            });


        }
    }

    private void getloactionupdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double latitude=location.getLatitude();
                        double longitude=location.getLongitude();
                       /* String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
                        Log.w("TAG","service message "+msg);*/
                      //  Toast.makeText(context,"location",Toast.LENGTH_SHORT).show();

                        /**
                         * Create a Geofence list by adding all fences you want to track
                         */

                        CallApi_Of_add_LOcation(latitude,longitude);
                        }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.v("TAG","onStatusChanged status"+status);
                        Log.v("TAG","onStatusChanged extras "+extras);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.v("TAG","onProviderEnabled "+provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.v("TAG","onProviderDisabled "+provider);
                    }
                });
    }

    private void CallApi_Of_add_LOcation(double lat, double lng) {

        DbModelSendingData task = new DbModelSendingData();
        task.setUserempId(pending_job_pref.getString(Constants.userIdKey, ""));
        task.setLat(String.valueOf(lat));
        task.setLng(String.valueOf(lng));
        task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
        task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
        task.setProjectId(pending_job_pref.getString("job_id","0"));
        

        if(pending_job_pref.getString("ot_started","0").equalsIgnoreCase("true"))
        {
            task.setStatus("3");
        }
        else if(pending_job_pref.getString("session_loggedin","0").equalsIgnoreCase("1")&&pending_job_pref.getString("timer_paused","false").equalsIgnoreCase("true")){
            task.setStatus("2");
        }
        else if(pending_job_pref.getString("session_loggedin","0").equalsIgnoreCase("1"))
        {
            task.setStatus("1");
        }

        else {
            task.setStatus("0");
        }

        if(pending_job_pref.getString("session_loggedin","0").equalsIgnoreCase("1")
                && pending_job_pref.getString("timer_paused","0").equalsIgnoreCase("false")) {
            AppDatabase db = AppDatabase.getDbInstance(context);
            db.taskDao().insert(task);

            Log.i("DB","Updating db");
            Log.i("DB","job id "+pending_job_pref.getString("job_id","0"));
        }
    }


    private String getDateWithFormat(long timeInMillis,boolean getTheDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        String formattedDateOrTime;

        if(getTheDate) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            formattedDateOrTime = format.format(calendar.getTime());

        } else {

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            formattedDateOrTime = df.format(calendar.getTime());
        }
        return formattedDateOrTime;


    }


   /* LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Log.w("TAG","service message "+msg);
            // Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
          //  CallApi_Of_add_LOcation(latitude,longitude);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void CallApi_Of_add_LOcation(double latitude, double longitude) {
    }*/

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();

    }

    protected void stopLocationUpdates() {
        if (locationManager != null && locationManager != null)
            locationManager.removeGpsStatusListener((GpsStatus.Listener) context);
        locationManager.removeUpdates((LocationListener) this);
        stopForeground(true);
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
                        .setSmallIcon(R.drawable.ic_play_icon)
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
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
    }

    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    /**
     * Create a Geofence list by adding all fences you want to track
     */
    public void createGeofences(double latitude, double longitude) {

      /*  String id = UUID.randomUUID().toString();
        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, 200) // Try changing your radius
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
        mGeofenceList.add(fence);*/

    }

}
