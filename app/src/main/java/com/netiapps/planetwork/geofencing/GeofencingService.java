package com.netiapps.planetwork.geofencing;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeofencingService extends Service  {

    private static final String TAG = "GeofencingService";
    private final IBinder binder = new LocalBinder();
    LocationManager locationManager;
    Context context;
    private List<Geofence> mGeofenceList;
    private GoogleApiClient mGoogleApiClient;
  //  double currentLatitude = 13.0212406, currentLongitude = 77.6472387;
    double currentLatitude = 0, currentLongitude = 0;
    LocationRequest mLocationRequest;
    String latt,longi;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class LocalBinder extends Binder {
        public GeofencingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GeofencingService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        latt= intent.getStringExtra("lattitude");
        longi= intent.getStringExtra("longitude");

        Log.v("TAG","Geofencing intent data "+latt +  " & "+longi);

        Thread thread = new Thread(new Mythreadclass());
        thread.start();

        if (Build.VERSION.SDK_INT > 25) {
            startRunningInForeground();
        }
        return Service.START_STICKY;
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
        mGeofenceList = new ArrayList<Geofence>();

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resp == ConnectionResult.SUCCESS) {

            initGoogleAPIClient();

            createGeofences(latt, longi);

        } else {
            Log.e(TAG, "Your Device doesn't support Google Play Services.");
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    public void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionAddListener)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
        mGoogleApiClient.connect();
    }

    private GoogleApiClient.ConnectionCallbacks connectionAddListener =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    Log.i(TAG, "onConnected");

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) context);

                    } else {
                        //If everything went fine lets get latitude and longitude
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();


                        Log.i(TAG, "LAT-LONG "+currentLatitude + " WORKS " + currentLongitude);

                        //createGeofences(currentLatitude, currentLongitude);
                        //registerGeofences(mGeofenceList);
                    }

                    /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            3000,
                            0, new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {

                                    currentLatitude = location.getLatitude();
                                    currentLongitude = location.getLongitude();
                                    Log.i(TAG, "LAT-LONG "+currentLatitude + " WORKS " + currentLongitude);

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
*/


                    try{
                        LocationServices.GeofencingApi.addGeofences(
                                mGoogleApiClient,
                                getGeofencingRequest(),
                                getGeofencePendingIntent()
                        ).setResultCallback(new ResultCallback<Status>() {

                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Log.i(TAG, "Saving Geofence");

                                } else {
                                    Log.e(TAG, "Registering geofence failed: " + status.getStatusMessage() +
                                            " : " + status.getStatusCode());
                                }
                            }
                        });

                    } catch (SecurityException securityException) {
                        // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                        Log.e(TAG, "Error");
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {

                    Log.e(TAG, "onConnectionSuspended");

                }
            };

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    Log.e(TAG, "onConnectionFailed");
                }
            };

    /**
     * Create a Geofence list
     */
    public void createGeofences(String latitude, String longitude) {
        double latt= Double.parseDouble(latitude);
        double langg=Double.parseDouble(longitude);
        String id = UUID.randomUUID().toString();
        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latt, langg, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)

                .build();
        mGeofenceList.add(fence);
        Log.v("TAG","mGeofenceList "+mGeofenceList);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
       /* if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }*/
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
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

                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
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
                new Intent(this, MainActivity.class), 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
