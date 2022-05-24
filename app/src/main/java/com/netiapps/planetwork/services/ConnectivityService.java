package com.netiapps.planetwork.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.utils.ConnectionHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectivityService extends Service {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    BroadcastReceiver receiver;

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    NotificationManager manager ;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       // YourTask();

       // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
       // filter.addAction("android.location.PROVIDERS_CHANGED");

         receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
              /*  LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (action.matches("aandroid.location.PROVIDERS_CHANGED")) {

                    boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if(!gpsEnabled) {
                        Toast.makeText(context, "Logout ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "GPS enabled ", Toast.LENGTH_SHORT).show();
                    }

                }*/

                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && ConnectionHelper.isOnline) {
                                ConnectionHelper.isOnline = false;
                             //   Toast.makeText(getApplicationContext(), "No conncetioion available", Toast.LENGTH_SHORT).show();
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

                                    Intent i = new Intent(context, DashBoardActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    i.putExtra("close_activity", true);
                                    i.putExtra("source", "internet");
                                    context.startActivity(i);
                               /* DashBoardActivity dashBoardActivity=new DashBoardActivity();
                                dashBoardActivity.finishACtivity();*/
                                }
                            }
                        }
                    } else {
                        Log.i("NETWORK123", "Connected");
                        ConnectionHelper.isOnline = true;
                    }
                }
            }
        };

        registerReceiver(receiver, filter);
        
        return Service.START_STICKY;
    }


    private void YourTask() {

         syncdata();

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
        if(receiver != null){
            try{
                unregisterReceiver(receiver);
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
