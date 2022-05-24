package com.netiapps.planetwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OfflineLocationCaptureActivity extends AppCompatActivity {
    LocationManager locationManager;
    Context mContext;
    TextView tvlatlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline_location_capture);
        mContext = this;

        tvlatlang=findViewById(R.id.tvlatlang);

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
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
                5, locationListenerGPS);
        isLocationEnabled();

    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg=" Lat: "+latitude + " Long: "+longitude;
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();

            String data=tvlatlang.getText().toString().trim();

            if(data==null){
                data=msg;
            }
            else {
                data=data+"\n"+msg;
            }
            tvlatlang.setText(data);


            CallApi_Of_add_LOcation(latitude,longitude);
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


    protected void onResume(){
        super.onResume();
        isLocationEnabled();
    }
    public void CallApi_Of_add_LOcation(double lat, double lng) {
        String curent_time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());


        DbModelSendingData task = new DbModelSendingData();
        task.setUserempId("112");
        task.setLat(String.valueOf(lat));
        task.setLng(String.valueOf(lng));
        task.setDate(curent_time);
        task.setTime(time);
        task.setStatus("10");


        AppDatabase db = AppDatabase.getDbInstance(OfflineLocationCaptureActivity.this);
        db.taskDao().insert(task);
        Log.w("TAG","Updating db");

       /* LatlangDatabse db = LatlangDatabse.getDbInstance(context);
        LatlangEntity latlangEntity= new LatlangEntity("6",current_lattitude,current_longitue,date,"1","1","10","1","1","1");

        if(pending_job_pref.getString("session_loggedin","0").equalsIgnoreCase("1")) {
            db.getUserDao().insert(latlangEntity);
            Log.w("TAG", "Updating db");
            Log.w("TAG", "longitude" + longitude + "-- latitude" + longitude);
        }*/

    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }}