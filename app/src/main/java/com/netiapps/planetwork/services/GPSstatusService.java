package com.netiapps.planetwork.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.utils.ConnectionHelper;
import com.netiapps.planetwork.utils.LocalHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GPSstatusService extends Service {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    BroadcastReceiver GPSreceiver;

    static final String CONNECTIVITY_CHANGE_ACTION = "android.location.PROVIDERS_CHANGED";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // YourTask();

       // Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();

       filter.addAction("android.location.PROVIDERS_CHANGED");

         GPSreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

               
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if(gpsEnabled) {
                       // Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    } else {
                     //   Toast.makeText(context, "No GPS service available ", Toast.LENGTH_SHORT).show();


                      /*  sharedPreferences=getApplicationContext().getSharedPreferences(com.netiapps.planetwork.utils.Constants.sharedPreferencesKey, MODE_PRIVATE);
                        mEditor = sharedPreferences.edit();
                        String current_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        mEditor.putString("forcestoptimer","1");
                        mEditor.putString("broadcast_logout_time",current_time);
                        mEditor.putString("timer_paused","true");
                        mEditor.apply();

                       Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);*/

                        context.sendBroadcast(new Intent("updatetext"));

                        sharedPreferences=getApplicationContext().getSharedPreferences(com.netiapps.planetwork.utils.Constants.sharedPreferencesKey, MODE_PRIVATE);
                        mEditor = sharedPreferences.edit();
                        String current_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                       String isloggedin=sharedPreferences.getString("session_loggedin","0");

                      if(isloggedin.equalsIgnoreCase("1")) {
                          mEditor.putString("forcestoptimer", "1");
                          mEditor.putString("broadcast_logout_time", current_time);
                          mEditor.putString("timer_paused", "true");
                          mEditor.apply();

                          String tittle=getApplicationContext().getString(R.string.logout) ;
                          String message=getApplicationContext().getString(R.string.location_disabled) ;

                          LocalHelper.showNotification(getApplicationContext(),tittle,message);

                        //  showNotification(getApplicationContext(),tittle,message);

                          Intent i = new Intent(context, DashBoardActivity.class);
                          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                          i.putExtra("close_activity", true);
                          i.putExtra("source", "internet");
                          context.startActivity(i);
                      }

                    }
                }

            }
        };

        registerReceiver(GPSreceiver, filter);
        
        return Service.START_STICKY;
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
        if(GPSreceiver != null){
            try{
                unregisterReceiver(GPSreceiver);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

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
