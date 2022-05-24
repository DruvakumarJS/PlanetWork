package com.netiapps.planetwork.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.utils.ConnectionHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class LiveLocation extends Service {

    Handler handler = new Handler();
    LocationListener locationListener;

    double lat,lng;
    private final int FIVE_SECONDS = 5000;

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    NotificationManager manager ;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                SmartLocation.with(getApplicationContext()).location().start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        scheduleSendLocation();
                    }
                });
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 1000);   // 1000*10*60 every 10 minut
        
        return Service.START_STICKY;
    }

    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                SmartLocation.with(getApplicationContext()).location().start((OnLocationUpdatedListener) locationListener);
                Log.v("TAG","lat = "+lat +" lang = "+lng);
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }
    private void syncdata() {

        LocationManager locMan = (LocationManager)getSystemService(LOCATION_SERVICE);
        if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Toast.makeText(getApplicationContext(), "GPS Service Available", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(getApplicationContext(), "No GPS Service", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


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

}
