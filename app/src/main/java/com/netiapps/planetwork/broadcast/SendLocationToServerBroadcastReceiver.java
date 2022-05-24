package com.netiapps.planetwork.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.database.UserLoginData;
import com.netiapps.planetwork.utils.ConnectionDetector;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.netiapps.planetwork.utils.Constants.isDebug;

public class  SendLocationToServerBroadcastReceiver extends BroadcastReceiver {

    Context mContext ;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        mEditor.putBoolean(Constants.LOCATION_SERVICE_SAVING_TO_SERVER_STARTED,true);
        mEditor.apply();

        // Testing
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
       // vibrator.vibrate(2000);

        List<DbModelSendingData> locationData = getAllData();
        List<UserLoginData> userlogindata = getuserlogindata();

        if(locationData.size() > 0){

            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                if(isDebug){
                    Log.d(Constants.LOG,"LocalDBSyncService : calledServer Sync UserData");
                }
                callServerForSyncUserData(locationData);
            }else{

                if(isDebug){
                    Log.d(Constants.LOG,"check Internet connection.");
                }
            }
        }else{
            if(isDebug){
                Log.d(Constants.LOG,"No value in DB");
            }
        }

        if(userlogindata.size() > 0){

            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                if(isDebug){
                    Log.d(Constants.LOG,"LocalDBSyncService : calledServer Sync UserData");
                }
                callServerForSyncUserloginData(userlogindata);
            }else{

                if(isDebug){
                    Log.d(Constants.LOG,"check Internet connection.");
                }
            }
        }else{
            if(isDebug){
                Log.d(Constants.LOG,"No value in DB");
            }
        }

    }



    public List<DbModelSendingData> getAllData(){

        List<DbModelSendingData> dbModelSendingDataList = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(mContext);
        dbModelSendingDataList = db.taskDao().getAllData();


        return dbModelSendingDataList;
    }

    public List<UserLoginData> getuserlogindata(){

        List<UserLoginData> userLoginData = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(mContext);
        userLoginData = db.logintaskDao().getlogindata();

        return userLoginData;
    }

    private void callServerForSyncUserData(List<DbModelSendingData> locationData) {

            JSONObject jsonObjectHeader = new JSONObject();

            try {
                final JSONArray serviceJSONArray = new JSONArray();

                for(int i = 0; i < locationData.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    DbModelSendingData dbModelSendingData = locationData.get(i);
                    jsonObject.put("user_id",dbModelSendingData.getUserempId());
                    jsonObject.put("date",dbModelSendingData.getDate());
                    jsonObject.put("time",dbModelSendingData.getTime());
                    jsonObject.put("latitude",dbModelSendingData.getLat());
                    jsonObject.put("longitude",dbModelSendingData.getLng());
                    jsonObject.put("job_id",dbModelSendingData.getProjectId());
                    jsonObject.put("status",dbModelSendingData.getStatus());
                    jsonObject.put("is_reached",dbModelSendingData.getIsreached());

                    serviceJSONArray.put(jsonObject);
                }
                jsonObjectHeader.put("data",serviceJSONArray);

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
                        if (status == 1) {
                            DbModelSendingData dbModelSendingData = locationData.get(locationData.size() - 1);

                            AppDatabase db = AppDatabase.getDbInstance(mContext);
                            db.taskDao().removeAllSavedLocationData(dbModelSendingData.getId() + "");

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

    private void callServerForSyncUserloginData(List<UserLoginData> userlogindata) {


        JSONObject jsonObjectHeader = new JSONObject();

        try {
            final JSONArray serviceJSONArray = new JSONArray();

            for(int i = 0; i < userlogindata.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                UserLoginData userLoginData = userlogindata.get(i);
                jsonObject.put("user_id",userLoginData.getUser_id());
                jsonObject.put("date",userLoginData.getDate());
                jsonObject.put("login_time",userLoginData.getLogin_time());
                jsonObject.put("logout_time",userLoginData.getLogout_time());

                serviceJSONArray.put(jsonObject);
            }
            jsonObjectHeader.put("data",serviceJSONArray);

            if(isDebug){
                Log.d(Constants.LOG, "LocalDBLOginSyncService : JSON Input params : "+jsonObjectHeader);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG, Constants.LOCATIONINSERT);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGINDETAILSINSERT,
                jsonObjectHeader, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) {

                        AppDatabase db = AppDatabase.getDbInstance(mContext);
                        db.logintaskDao().removeAllSavedLoginData();

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
                Log.v("TAG","DB insertion response error "+ ErrorUtil.getTheErrorJSONObject(error));
                //SQLSTATE[01000]: Warning: 1265 Data truncated for column 'travel_distance' at row 1 (SQL: update `work_reports` set `travel_distance` = NAN, `end` = 18:14:54, `work_reports`.`updated_at` = 2022-04-28 18:34:17 where `id` = 5)

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

}
