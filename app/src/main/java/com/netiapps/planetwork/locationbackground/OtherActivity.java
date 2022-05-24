package com.netiapps.planetwork.locationbackground;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.netiapps.planetwork.R;

public class OtherActivity extends AppCompatActivity implements View.OnClickListener, LocationService_callback{

    private static final int PERMISSION_REQUEST_CODE = 202;
    private Context context;
    GetLocation_Service mService;
    boolean mBound = false;
    GpsStatusReceiver receiver = new GpsStatusReceiver();
    private final String PACKAGE_URL_SCHEME = "package:";

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GetLocation_Service.LocalBinder binder = (GetLocation_Service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
           // mService.setCallbacks(OtherActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_other);

        registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean result = checkPermissions();
            if (result) {
                bindLocationService();
            }
        }

        checkLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(OtherActivity.this, PERMISSIONS)) {
            Functions.customAlertDialog(OtherActivity.this, new Callback() {
                @Override
                public void Responce(String resp) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }
            });
        } else {
            return true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            bindLocationService();
        }

        return false;
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    private void bindLocationService() {
        GetLocation_Service locationService = new GetLocation_Service();
        Intent mServiceIntent = new Intent(context, locationService.getClass());
        if (context != null && !Functions.isMyServiceRunning(context, locationService.getClass())) {
            context.startService(mServiceIntent);
            context.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDataReceived(LatLng data) {

    }

    private void unbindLocationService() {
        GetLocation_Service locationService = new GetLocation_Service();
        if (Functions.isMyServiceRunning(context, locationService.getClass())) {
            context.unbindService(mConnection);
        }
    }
    // if user does not permitt the app to get the location then we will go to the enable location screen to enable the location permission
    private void enableLocation() {
        startActivity(new Intent(OtherActivity.this, Enablelocation_A.class));
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    private void checkLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        boolean isEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isEnabled) {
            enableLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindLocationService();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    } else {
                        Functions.customAlertDialogDenied(OtherActivity.this, new Callback() {
                            @Override
                            public void Responce(String resp) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                                startActivity(intent);
                            }
                        });
                    }
                }
                break;
        }
    }

    private class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkLocation();
        }
    }
}
