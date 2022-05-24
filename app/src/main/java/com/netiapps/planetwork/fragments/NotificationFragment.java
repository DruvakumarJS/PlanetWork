package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.ProfileActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.adapter.AdapterNotifications;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.model.NotificationModel;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;


public class NotificationFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView imgBcak;
    private SharedPreferences sharedPreferences;
    private RecyclerView rvNotificationlist;
    SwipeRefreshLayout swipeRefreshLayout;
    List<NotificationModel>notificationList;
    private AdapterNotifications adapterNotifications;
    private LottieAnimationView anim_nodatfound,anim_loading;


    private TextView header;
    private CardView cardbackbtn;
    private CircleImageView cardivprofile;

    String notificartionjson="{\n" +
            "  \"status\": 1,\n" +
            "  \"jobs\": [\n" +
            "    {\n" +
            "      \"id\": \"11\",\n" +
            "      \"tittle\": \"Leave Approved\",\n" +
            "      \"description\": \"Your Request for leave has been approved\",\n" +
            "      \"date\":\"10 Feb 2022\"\n" +
            "      \n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"11\",\n" +
            "      \"tittle\": \"Welcome to PlanetWork\",\n" +
            "      \"description\": \"PlanetWork Welcomes you , a simplified application to all smart workers\",\n" +
            "       \"date\":\"11 jan 2022\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.notification_list, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

       /* imgBcak = view.findViewById(R.id.iv_back);
        imgBcak.setOnClickListener(this);*/
        notificationList=new ArrayList<>();

        cardbackbtn=view.findViewById(R.id.cardimage);
        header=view.findViewById(R.id.tvheader);
        cardivprofile=view.findViewById(R.id.ivcircleimg);
        anim_nodatfound=view.findViewById(R.id.ivnodatafound);
        anim_loading=view.findViewById(R.id.loading);

        header.setText("Notification");
        cardivprofile.setVisibility(View.VISIBLE);
        //cardivprofile.setBackgroundResource(R.drawable.ic_person);
        rvNotificationlist=view.findViewById(R.id.rvnotificationlits);
        swipeRefreshLayout=view.findViewById(R.id.swipecontainer);


        Picasso.get().load(R.drawable.ic_person).into(cardivprofile);


        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);


        rvNotificationlist.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        getNotifications();
        rvNotificationlist.setItemAnimator(new DefaultItemAnimator());

        cardivprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationList.clear();

                if(LocalHelper.isConnectedToInternet(getActivity())) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNotifications();
                        }
                    },2000);

                }
                else {
                    anim_loading.setVisibility(View.GONE);
                    anim_nodatfound.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                   // Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

                }
            }
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_back:

                break;
        }

    }

    public List<DbModelSendingData> getAllData(){

        List<DbModelSendingData> dbModelSendingDataList = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(getActivity());
        dbModelSendingDataList = db.taskDao().getAllData();

        return dbModelSendingDataList;
    }


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
                   // jsonObject.put("project_id",dbModelSendingData.getProjectId());
                    jsonObject.put("status",dbModelSendingData.getStatus());

                    serviceJSONArray.put(jsonObject);

                }

                jsonObjectHeader.put("data",serviceJSONArray);

                Log.d(Constants.LOG, "LocalDBSyncService : JSON Input params : "+ jsonObjectHeader);

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
                            DbModelSendingData dbModelSendingData = mServiceList.get(mServiceList.size() - 1);

                            AppDatabase db = AppDatabase.getDbInstance(getActivity());
                            db.taskDao().removeAllSavedLocationData(dbModelSendingData.getId() + "");

                            swipeRefreshLayout.setRefreshing(false);
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
                    swipeRefreshLayout.setRefreshing(false);
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

            PlanetWorkVolleySingleton.getInstance(getContext()).addToRequestQueue(mRequest);


        }catch(Exception e) {
            e.printStackTrace();
        }

    }


//    private void getTheNotification() {
//
//
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("logged_user_id", sharedPreferences.getString(Constants.userIdKey, "null"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.v(Constants.LOG, jsonObject.toString());
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
//        if(displayProgressBar){
//            progressDialog.show();
//        }
//        Log.d(Constants.LOG, Constants.TOTAL_CONSG_COUNT);
//
//        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.TOTAL_CONSG_COUNT,
//                jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(Constants.LOG, response.toString());
//                if(displayProgressBar){
//                    progressDialog.dismiss();
//                }
//
//                try {
//                    JSONObject jsonObject1 = new JSONObject(String.valueOf(response));
//                    int status = jsonObject1.getInt("status");
//                    if (status == 1) {
////
//
//                        tvDeliveredCount.setText("Delivered :  " + jsonObject1.getString("delivered_count"));
//                        tvTotalcount.setText("Total : " + jsonObject1.getString("total_count"));
//                        tvPending.setText("Pending : " + jsonObject1.getString("pending_count"));
//                        tvReturneddCount.setText("Returned : " + jsonObject1.getString("returned_count"));
//
//
//                    } else {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if(displayProgressBar){
//                    progressDialog.dismiss();
//                }
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                   // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
//                    return;
//                }
//                Log.d("RESPONSE ERROR", error.toString());
//
//               // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String, String> mHeaders = new HashMap<>();
//                mHeaders.put("Content-Type", "application/json");
//                mHeaders.put("Accept", "application/json");
//                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));
//
//                return mHeaders;
//            }
//
//        };
//
//        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);
//
//    }


    private void getNotifications() {

        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));


        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));


        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getchartdata(mHeaders,listDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if(notificationList!=null)
                {
                    notificationList.clear();
                }

               // String result=new Gson().toJson(response.body());
                String result=notificartionjson;

                try {
                    JSONObject jsonObject=new JSONObject(result);

                    String status=jsonObject.getString("status");
                    if(status.equalsIgnoreCase("1")){
                        JSONArray jsonArray=jsonObject.getJSONArray("jobs");

                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject js=jsonArray.getJSONObject(i);
                            String id=js.getString("id");
                            String tittle=js.getString("tittle");
                            String message=js.getString("description");
                            String date=js.getString("date");

                            NotificationModel model=new NotificationModel();
                            model.setId(id);
                            model.setTittle(tittle);
                            model.setDescription(message);
                            model.setDate(date);

                            notificationList.add(model);

                        }

                        adapterNotifications=new AdapterNotifications(getActivity(),notificationList);
                        rvNotificationlist.setAdapter(adapterNotifications);
                        rvNotificationlist.setVisibility(View.VISIBLE);
                        anim_loading.setVisibility(View.GONE);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
               // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.VISIBLE);

            }
        });


    }


}
