package com.netiapps.planetwork.services;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationService extends Service {

    private static final int ONE_SECOND = 1000;
    private static final int LOCATION_INTERVAL = 3 * ONE_SECOND;
    private Handler mHandler;
    private GpsTracker gpsTracker;
    private SharedPreferences sharedPreferences;
    private String userID;


    @Override
    public void onCreate() {
        sharedPreferences = (SharedPreferences) this.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        userID = sharedPreferences.getString(Constants.userIdKey, "");

        mHandler = new Handler();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mHandler.postDelayed(mRunnable, LOCATION_INTERVAL);
        return START_STICKY;
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            getLocation();
        }
    };



    public void getLocation(){
        gpsTracker = new GpsTracker(LocationService.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
//            tvlat.setText(String.valueOf(latitude));
//            tvlon.setText(String.valueOf(longitude));
            SaveTask st = new SaveTask();
            st.execute(latitude,longitude);
        }else{
           // showSettingsAlert(tvlat,tvlon);
            mHandler.removeCallbacks(mRunnable);
            stopSelf();
        }
    }


    public void showSettingsAlert(TextView lat , TextView lon) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationService.this);
        builder.setTitle("GPS Settings");
        builder.setMessage("GPS is not enabled. Please enable GPS.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        });


        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    class SaveTask extends AsyncTask<Double, Double, Void> {


        @Override
        protected Void doInBackground(Double... doubles) {

            
            //creating a task
            DbModelSendingData task = new DbModelSendingData();
            task.setUserempId(userID);
            task.setLat(String.valueOf(doubles[0]));
            task.setLng(String.valueOf(doubles[1]));
            task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
            task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
            task.setStatus(sharedPreferences.getString("StartPlayOn",""));

            //adding to database
            AppDatabase.getDbInstance(LocationService.this)
                       .taskDao()
                       .insert(task);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHandler.postDelayed(mRunnable, LOCATION_INTERVAL);

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



}
