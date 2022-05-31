package com.netiapps.planetwork.rough;

import static com.netiapps.planetwork.utils.Constants.isDebug;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.broadcast.SendLocationToServerBroadcastReceiver;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button alarmbutton, cancelButton;
    EditText text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;
    Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private int finalsize=5;
    List<DbModelSendingData> DBlocationData=new ArrayList<>();

    int  ELAPSED_TIME_TO_SAVE_IN_SERVER = 10 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_extra);
        alarmbutton = (Button) findViewById(R.id.button);
        cancelButton = (Button) findViewById(R.id.button2);
        text = (EditText) findViewById(R.id.editText);
        alarmbutton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        mContext=getApplicationContext();
        sharedPreferences = mContext.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

       /* mEditor.putBoolean(Constants.LOCATION_SERVICE_SAVING_TO_SERVER_STARTED,true);
        mEditor.apply();
*/
        /* use this method if you want to start Alarm at particular time*/

        // startAlertAtParticularTime();

    }

    // This method to be called at Start button click and set repeating at every 10 seconds interval

    public void startAlert(View view) {
        if (!text.getText().toString().equals("")) {
            int i = Integer.parseInt(text.getText().toString());
            intent = new Intent(this, SendLocationToServerBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + (i * 1000), ELAPSED_TIME_TO_SAVE_IN_SERVER
                    , pendingIntent);

            Toast.makeText(this, "Alarm will set in " + i + " seconds",
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Please Provide time ",Toast.LENGTH_SHORT).show();
        }

    }

    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval

        intent = new Intent(this, SendLocationToServerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 40);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);

        Toast.makeText(this, "Alarm will vibrate at time specified",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
           // startAlert(v);
            sendData();
        } else {
            if (alarmManager != null) {

                alarmManager.cancel(pendingIntent);
                Toast.makeText(this, "Alarm Disabled !!",Toast.LENGTH_LONG).show();

            }

        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void sendData() {

        DBlocationData = getAllData();
        callServerForSyncUserData(DBlocationData);
    }

    private void callServerForSyncUserData(List<DbModelSendingData> locationData) {

        JSONObject jsonObjectHeader = new JSONObject();

        try {
            final JSONArray serviceJSONArray = new JSONArray();
            int listsize=locationData.size();

            if(listsize>0) {

                for (int i = 0; i < finalsize; i++) {
                    JSONObject jsonObject = new JSONObject();
                    DbModelSendingData dbModelSendingData = locationData.get(i);
                    jsonObject.put("user_id", dbModelSendingData.getUserempId());
                    jsonObject.put("date", dbModelSendingData.getDate());
                    jsonObject.put("time", dbModelSendingData.getTime());
                    jsonObject.put("latitude", dbModelSendingData.getLat());
                    jsonObject.put("longitude", dbModelSendingData.getLng());
                    jsonObject.put("job_id", dbModelSendingData.getProjectId());
                    jsonObject.put("status", dbModelSendingData.getStatus());
                    jsonObject.put("is_reached", dbModelSendingData.getIsreached());

                    serviceJSONArray.put(jsonObject);
                }
                jsonObjectHeader.put("data", serviceJSONArray);
            }
            else{
                Log.d(Constants.LOG,"No value in DB");
            }

            if(isDebug){
                Log.d(Constants.LOG, "LocalDBSyncService : JSON Input params : "+jsonObjectHeader);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG, Constants.LOCATIONINSERT);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOCATIONINSERT,
                jsonObjectHeader, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                try {
                    int status = response.getInt("status");
                    int jjj=0;
                    if (status == 1) {
                        //DbModelSendingData dbModelSendingData = locationData.get(locationData.size() - 1);
                        DbModelSendingData dbModelSendingData = DBlocationData.get(finalsize - 1);

                       /*  jjj=dbModelSendingData.getId();
                        Log.v("TAG","id is "+jjj);
*/
                        AppDatabase db = AppDatabase.getDbInstance(mContext);
                        db.taskDao().removeAllSavedLocationData(dbModelSendingData.getId() + "");

                        DBlocationData = getAllData();
                        if(DBlocationData.size()>0) {
                            callServerForSyncUserData(DBlocationData);
                        }
                        else{
                            Log.d(Constants.LOG,"DB is empty ");
                        }
                        String todayDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                        mEditor.putString("db_sync_time",todayDate);
                        mEditor.commit();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //  AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());

                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                        //  AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", "");
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                        return;

                    }
                }

                //  AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());
                Toast.makeText(mContext, "Failed"

                        + ErrorUtil.getTheErrorJSONObject(error), Toast.LENGTH_SHORT).show();
                Log.v("TAG","LOGIN DB insertion response error "+ ErrorUtil.getTheErrorJSONObject(error));
                //SQLSTATE[01000]: Warning: 1265 Data truncated for column 'travel_distance' at row 1 (SQL: update `work_reports` set `travel_distance` = NAN, `end` = 18:14:54, `work_reports`.`updated_at` = 2022-04-28 18:34:17 where `id` = 5)
                 /*   DbModelSendingData dbModelSendingData = locationData.get(locationData.size() - 1);

                    AppDatabase db = AppDatabase.getDbInstance(mContext);
                    db.taskDao().removeAllSavedLocationData(dbModelSendingData.getId() + "");*/
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));
                return mHeaders;
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                60 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(mContext).addToRequestQueue(mRequest);


    }


    public List<DbModelSendingData> getAllData(){

        List<DbModelSendingData> dbModelSendingDataList = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(mContext);
        dbModelSendingDataList = db.taskDao().getAllData();


        return dbModelSendingDataList;
    }
}
