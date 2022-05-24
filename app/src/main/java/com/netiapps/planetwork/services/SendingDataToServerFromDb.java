package com.netiapps.planetwork.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.utils.ConnectionDetector;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendingDataToServerFromDb  extends Service {

    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private String userId;
    private AppDatabase db ;
    private static final int SYNC_INTERVAL = 1 * 60 * 1000;


    @Override
    public void onCreate() {
        sharedPreferences = (SharedPreferences) this.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.userIdKey,"");

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, SYNC_INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.postDelayed(mRunnable, SYNC_INTERVAL);
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.LOG,"LocalDBSyncService : OnDestroy");
        mHandler.removeCallbacks(mRunnable);
    }

      public List<DbModelSendingData> getAllData(){

        List<DbModelSendingData> dbModelSendingDataList = new ArrayList<>();
          AppDatabase db = AppDatabase.getDbInstance(SendingDataToServerFromDb.this);
          dbModelSendingDataList = db.taskDao().getAllData();

          return dbModelSendingDataList;
      }

    private class GetUsersAsyncTask extends AsyncTask<Void, Void,List<DbModelSendingData>>
    {
        @Override
        protected List<DbModelSendingData> doInBackground(Void... url) {
            return db.taskDao().getAllData();
        }
    }

    public Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            Log.d(Constants.LOG,"LocalDBSyncService : Runnable");


            if (new ConnectionDetector(SendingDataToServerFromDb.this).isConnectingToInternet()) {

                Log.d(Constants.LOG,"LocalDBSyncService : calledServer Sync UserData");

                callServerForSyncUserData();
            }

            mHandler.postDelayed(mRunnable, SYNC_INTERVAL);
        }
    };

    private void callServerForSyncUserData() {

        try {

            JSONObject jsonObjectHeader = new JSONObject();
            List<DbModelSendingData> mServiceList = getAllData();

            try {
                final JSONArray serviceJSONArray = new JSONArray();

                for(int i = 0; i < mServiceList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    DbModelSendingData dbModelSendingData = mServiceList.get(i);
                    jsonObject.put("user_id",dbModelSendingData.getUserempId());
                    jsonObject.put("date",dbModelSendingData.getDate());
                    jsonObject.put("time",dbModelSendingData.getTime());
                    jsonObject.put("lat",dbModelSendingData.getLat());
                    jsonObject.put("lng",dbModelSendingData.getLng());
                    jsonObject.put("project_id",dbModelSendingData.getProjectId());
                    jsonObject.put("status",dbModelSendingData.getStatus());

                    serviceJSONArray.put(jsonObject);

                }

                jsonObjectHeader.put("data",serviceJSONArray);
                Log.d(Constants.LOG, "LocalDBSyncService : JSON Input params : "+jsonObjectHeader);

            } catch (JSONException e) {
                e.printStackTrace();
            }



            Log.d(Constants.LOG, Constants.LOCATIONINSERT);
            JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOCATIONINSERT,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(Constants.LOG, response.toString());
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                           DbModelSendingData dbModelSendingData = mServiceList.get(mServiceList.size() - 1);

                            AppDatabase db = AppDatabase.getDbInstance(SendingDataToServerFromDb.this);
                            db.taskDao().removeAllSavedLocationData(dbModelSendingData.getId() + "");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Log.d("Data",error.toString());
                        return;
                    }
                    Log.d("RESPONSE ERROR", error.toString());
                    // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

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

            PlanetWorkVolleySingleton.getInstance(SendingDataToServerFromDb.this).addToRequestQueue(mRequest);


        }catch(Exception e) {
            e.printStackTrace();
        }

    }


}
