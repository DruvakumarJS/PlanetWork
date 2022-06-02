package com.netiapps.planetwork.locationbackground;


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
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.roomdatabase.latlangDAO;
import com.netiapps.planetwork.utils.Constants;

import static android.content.ContentValues.TAG;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetLocation_Service extends Service {

    private final IBinder binder = new LocalBinder();
    SharedPreferences pending_job_pref;
    SharedPreferences.Editor mEditor;
    double latitude;
    double longitude;
    LatLng latLng;
    Context context;
    String user_id;
    LocationCallback locationCallback;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 5000;
    private int FATEST_INTERVAL = 3000;
    private int DISPLACEMENT = 0;
    private LocationService_callback serviceCallbacks;
    private FusedLocationProviderClient mFusedLocationClient;

    private latlangDAO latlangDAO;
    Handler locationhandler = new Handler();
    int FIVE_SECONDS=5000;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "GetLocation_Service running");
        createLocationRequest();
        context = getApplicationContext();

        pending_job_pref = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = pending_job_pref.edit();

        user_id = pending_job_pref.getString("1", "");
    }

    public void setCallbacks(LocationService_callback callbacks) {
        serviceCallbacks = callbacks;
    }

    public void setCallbacks(DashBoardActivity dashBoardActivity) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Thread thread = new Thread(new GetLocation_Service.Mythreadclass());
        thread.start();

        if (Build.VERSION.SDK_INT > 25) {
            startRunningInForeground();
        }

        return Service.START_STICKY;
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        startLocationUpdates();
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult : is empty.");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLastLocation = location;
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();

                        latLng = new LatLng(latitude, longitude);
                        if (serviceCallbacks != null)
                            serviceCallbacks.onDataReceived(new LatLng(latitude, longitude));

                        String timer_paused=pending_job_pref.getString("timer_paused","false");

                        CallApi_Of_add_LOcation(latitude, longitude);

                       // upload_to_sharedPreference(new LatLng(latitude, longitude));
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback
                , Looper.myLooper());

    }

    protected void stopLocationUpdates() {
        if (mFusedLocationClient != null && locationCallback != null)
            mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void upload_to_sharedPreference(LatLng latLng) {
        double lat = (latLng.latitude);
        double lon = (latLng.longitude);

        SharedPreferences.Editor editor = pending_job_pref.edit();
        editor.putString("lat", Double.toString(lat));
        editor.putString("long", Double.toString(lon));
        editor.apply();
    }

    @Override
    public void onDestroy() {

        stopLocationUpdates();

        super.onDestroy();
    }

    public void CallApi_Of_add_LOcation(double lat, double lng) {

        DbModelSendingData task = new DbModelSendingData();
        task.setUserempId(pending_job_pref.getString(Constants.userIdKey, ""));
        task.setLat(String.valueOf(lat));
        task.setLng(String.valueOf(lng));
        task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
        task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
        task.setProjectId(pending_job_pref.getString("job_id","0"));

        if(pending_job_pref.getBoolean("insideGeofence",false) &&
                pending_job_pref.getString("job_id","0").equalsIgnoreCase(pending_job_pref.getString("geofencetaskid","null")))
        {
            task.setIsreached("true");
        }
        else
        {
            task.setIsreached("false");
        }

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

            Log.i("DB","Updating db geofence task id from getLocation Service");
            //   Log.i("DB","inside Geofence "+pending_job_pref.getBoolean("insideGeofence",false));
            //  Log.i("DB","job id "+pending_job_pref.getString("job_id","0"));
        }


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
                    .setSmallIcon(R.drawable.ic_play_icon)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
    }

    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DashBoardActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pause_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    public void setCallbacks(FragmentActivity activity) {
    }


    public class LocalBinder extends Binder {
        public GetLocation_Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return GetLocation_Service.this;
        }
    }

    final class Mythreadclass implements Runnable {

        @Override
        public void run() {
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

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
    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}


