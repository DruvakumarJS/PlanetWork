package com.netiapps.planetwork.locationbackground;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.utils.Constants;


public class Enablelocation_A extends AppCompatActivity {

    TextView enable_location_btn;
    SharedPreferences sharedPreferences;
    Boolean isFristTime = true;
    public Enablelocation_A() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_enable_location);
        gpsStatus();
        sharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);

        enable_location_btn = findViewById(R.id.enable_location_btn);
        enable_location_btn.setOnClickListener(v -> gpsStatus());

        findViewById(R.id.ic_back_btn).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
        });

    }

    public void gpsStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            isFristTime = false;
            new GpsUtils(Enablelocation_A.this).turnGPSOn(isGPSEnable ->  finish());
        } else {
            finish();
            overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isFristTime){
            gpsStatus();
        }
    }
}

