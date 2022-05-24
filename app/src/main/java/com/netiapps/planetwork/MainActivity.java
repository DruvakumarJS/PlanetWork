package com.netiapps.planetwork;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amitshekhar.DebugDB;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.netiapps.planetwork.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHECK_SETTINGS = 1;
    public static boolean loginStatus = false;
    private CircularProgressButton btnlogin;
    private String androidId;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tvset , tvVersion;
    String IMEINumber;
    private static final int REQUEST_CODE = 101;
    Handler handler = new Handler();

    double lat,lng;
    private final int FIVE_SECONDS = 5000;


    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;
    int  ELAPSED_TIME_TO_SAVE_IN_SERVER = 5 * 60 * 1000;
    LocationListener locationListener;
    int versionCode;
    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DB IP", DebugDB.getAddressLog());

        sharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        loginStatus = sharedPreferences.getBoolean(Constants.loginStatusKey, false);
        editor = sharedPreferences.edit();

      //  Log.v("TAG","Login status"+loginStatus);

        Log.d("session",sharedPreferences.getString("session_loggedin",""));

        btnlogin = findViewById(R.id.cirGetStartedButton);
        btnlogin.setOnClickListener(this);
        tvset= findViewById(R.id.tvdeviceID);
        tvVersion=findViewById(R.id.tvversion);

        versionCode = BuildConfig.VERSION_CODE;
        versionName = BuildConfig.VERSION_NAME;

        tvVersion.setText(versionName);

        tvset.setOnClickListener(this);
        tvVersion.setOnClickListener(this);

        String curent_time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        checkTheRunTimePermissions();

        // only dynamic link data
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }


                        // Handle the deep link. For example, open the linked content,
                        // or apply promotional credit to the user's account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });

       /* Button crashButton = new Button(this);
        crashButton.setText("Test Crash");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
*/
    //  startService(new Intent(MainActivity.this, LiveLocation.class));
/*

        SmartLocation.with(this).location().start(new OnLocationUpdatedListener() {

            @Override
            public void onLocationUpdated(Location location) {
                 lat = location.getLatitude();
                 lng = location.getLongitude();

                scheduleSendLocation();
            }
        });
*/


        String broadcastimer=sharedPreferences.getString("broadcast_time","00");
        Log.v("TAG","broadcastimer"+broadcastimer);

        String fcmtoken=sharedPreferences.getString("fcm_token","");
        Log.v("TAG","fcm_token : "+fcmtoken);


        if(!sharedPreferences.getBoolean(Constants.LOCATION_SERVICE_SAVING_TO_SERVER_STARTED,false)){

        }

    }
    public void scheduleSendLocation() {
        handler.postDelayed(new Runnable() {
            public void run() {
                SmartLocation.with(MainActivity.this).location().start((OnLocationUpdatedListener) locationListener);
                Toast.makeText(MainActivity.this,"lat"+lat +"lang"+lng,Toast.LENGTH_SHORT).show();

                Log.v("TAG","lat"+lat +"lang"+lng);
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }






    private String getImei(){
        androidId = android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.v("IMEI", androidId);
        return androidId;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cirGetStartedButton:

              //  SmartLocation.with(this).activityRecognition().stop();

              /*  if(!isConnectedToInternet(MainActivity.this)){

                    showinternetrequestdiagouge();
                }
                else*/ if(!CheckGpsStatus()){
                   // Toast.makeText(MainActivity.this, "Please Connect to  GPS", Toast.LENGTH_SHORT).show();
             //  showGPSrequestdiagouge();
                    enableLocationSettings();
                }

              else if (loginStatus){

                       Intent i = new Intent(MainActivity.this, DashBoardActivity.class);
                    //Intent i = new Intent(MainActivity.this, OfflineLocationCaptureActivity.class);
                        i.putExtra("IMEINumber", androidId);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.putExtra("IMEINumber", androidId);
                        startActivity(i);
                        finish();
                    }

//                    Intent i = new Intent(MainActivity.this, DashBoardActivity.class);
//                    i.putExtra("IMEINumber", androidId);
//                    startActivity(i);


                break;

            case R.id.tvdeviceID:
                String messgae = "Hi Sir/Madam," +
                        "Please Find the DeviceId/IMEI for Registartion of PlanetWork  " + "  :  " + androidId;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = messgae;
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Device Registarion");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.tvversion:


            break;
        }
    }

    private void showinternetrequestdiagouge() {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("You need internet connection for this app. Please turn on mobile network or Wi-Fi in Settings.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(i);
                       // dialog.cancel();

                       /* Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent1);*/
                    }
                });

        builder1.setNegativeButton(
        "No thanks",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void checkTheRunTimePermissions() {

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted
            requestThePermissions();
            return;
        }

        readTheIMEI();
       // getImei();


    }
    public boolean CheckGpsStatus() {

        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;
    }

    protected void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                //user clicked OK, you can startUpdatingLocation(...);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                if (loginStatus){
                    Intent i = new Intent(MainActivity.this, DashBoardActivity.class);
                    i.putExtra("IMEINumber", androidId);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    i.putExtra("IMEINumber", androidId);
                    startActivity(i);
                    finish();

                }

                    }
                },2000);


            } else {
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }
    }

    private void requestThePermissions() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                      },
                REQUEST_CODE);    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // getImei();
                readTheIMEI();

            } else {
                Toast.makeText(this, "You cannot able to work without enable of permissions.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showAlertDialogWhenLogout(Boolean check) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage("Are you sure want to Logout.\nWould you " +
                "like to check again ...?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //performSuccessfulSwipeReverse();
                //fetchTheAutomatedConsolidatedEWBNumber();
            }
        });
        mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                buttonGenearateEWB2Number.setVisibility(View.VISIBLE);
//                tvAutomateTheCEWBNumber.setVisibility(View.GONE);
            }
        });

        AlertDialog mAlertDialog = mBuilder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();

    }

    private void readTheIMEI(){

        try {
            TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            IMEINumber = tManager.getDeviceId();
            tvset.setText("DeviceId/IMEI : " + IMEINumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                IMEINumber = getImei();
                tvset.setText("DeviceId/IMEI : " + IMEINumber);
            }
            tvset.setText("DeviceId/IMEI : " + IMEINumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().getExtras() != null) {


            Log.v("TAG","Data is "+getIntent().getExtras());
        }
        }
    }
