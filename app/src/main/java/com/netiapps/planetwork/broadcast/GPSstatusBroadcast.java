package com.netiapps.planetwork.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class GPSstatusBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(gpsEnabled && networkEnabled) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Logout ", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
