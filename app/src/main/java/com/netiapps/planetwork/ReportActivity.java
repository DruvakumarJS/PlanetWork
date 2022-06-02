package com.netiapps.planetwork;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.netiapps.planetwork.model.IdealLocation;
import com.netiapps.planetwork.model.ReportDetails;
import com.netiapps.planetwork.model.ReportModel;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.MapAnimator;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    String newDate, startTime , endTime, newdistance,newfromaddress,newtoaddress;
    String newUserId;
    private RelativeLayout ll_routeaddress;
    private RelativeLayout linear_routeaddresss;
    private ImageView img_routeright;
    private LinearLayout ll_mapaddress;
    private ImageView img_maprightt;
    private RelativeLayout linear_mapaddress;
    private List<ReportDetails> reportDetails;
    private List<IdealLocation> idealLocationss;
    private TextView tvstartAddress;
    private TextView tvendAddress;
    private TextView tvdistance;
    private TextView tvidellocation;
    private Context context;
    private TextView tvllocation;
    private GoogleMap mMap;
    List<LatLng> latLngList;
    private ArrayList<LatLng> locationArrayList;
    private LinearLayout ll_addressVerificationContent;
    private TextView header;
    private  ImageView ivBack;
    private RelativeLayout rlrootmap;
    private LottieAnimationView anim_loading;
    private LottieAnimationView anim_nodatafound;
    private  String todayDate;
   // SwipeRefreshLayout swipeRefreshLayout;

    String result="{\"status\":1,\"data\":[{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"13.0228\",\n" +
            "  \"longitude\": \"77.6521\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},\n" +
            "{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.9767\",\n" +
            "  \"longitude\": \"77.5713\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},\n" +
            "{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.8997\",\n" +
            "  \"longitude\": \"77.4827\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},\n" +
            "{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.7209\",\n" +
            "  \"longitude\": \"77.2799\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},\n" +
            "{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.5867\",\n" +
            "  \"longitude\": \"77.0453\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},\n" +
            "{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.5218\",\n" +
            "  \"longitude\": \"76.8951\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "},{\n" +
            "  \"id\": 222467,\n" +
            "  \"user_id\": 5,\n" +
            "  \"date\": \"2022-02-11\",\n" +
            "  \"time\": \"10:20:23\",\n" +
            "  \"latitude\": \"12.2958\",\n" +
            "  \"longitude\": \"76.6394\",\n" +
            "  \"status\": 1,\n" +
            "  \"created_at\": \"2022-02-11T04:50:24.000000Z\",\n" +
            "  \"updated_at\": \"2022-02-11T04:50:24.000000Z\"\n" +
            "}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandablelist);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        todayDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newDate = null;
                newUserId = null;
                startTime=null;
                endTime=null;
                newdistance=null;
                newfromaddress=null;
                newtoaddress=null;

            } else {
                newDate = extras.getString("date");
                newUserId = extras.getString("userId");
                startTime=extras.getString("start_time");
                endTime=extras.getString("end_time");
                newdistance=extras.getString("distance");
                newfromaddress=extras.getString("from_address");
                newtoaddress=extras.getString("to_address");

            }
        }

        reportDetails = new ArrayList<>();
        idealLocationss = new ArrayList<>();
        locationArrayList = new ArrayList<>();


        ll_routeaddress = findViewById(R.id.ll_routeaddress);
        linear_routeaddresss = findViewById(R.id.linear_routeaddresss);
        img_routeright = findViewById(R.id.img_right);
        ll_mapaddress = findViewById(R.id.ll_mapaddress);
        img_maprightt = findViewById(R.id.img_rightt);
        linear_mapaddress = findViewById(R.id.linear_mapaddress);
        tvendAddress = findViewById(R.id.tvtoaddress);
        tvstartAddress = findViewById(R.id.tvstartaddress);
        tvdistance = findViewById(R.id.tvdistance);
        tvidellocation = findViewById(R.id.tvidellocation);
        ll_addressVerificationContent = findViewById(R.id.ll_addressVerificationContent);
        header=findViewById(R.id.tvheader);
        ivBack=findViewById(R.id.ivback);
        rlrootmap=findViewById(R.id.rlroutemap);
        anim_loading=findViewById(R.id.loading);
        anim_nodatafound=findViewById(R.id.nodata);

        header.setText("Trip Analysis");
        rlrootmap.setVisibility(View.GONE);

        tvdistance.setText(newdistance +" km");
        tvstartAddress.setText(newfromaddress);
        tvendAddress.setText(newtoaddress);

        linear_mapaddress.setOnClickListener(this);
        linear_routeaddresss.setOnClickListener(this);
        tvidellocation.setOnClickListener(this);

       // getTheReportOfParctularFromServer();
        getTheLatandLong();

        toMap();
        toRoute();
    //    ll_mapaddress.setVisibility(View.GONE);
        ll_addressVerificationContent.setVisibility(View.GONE);
        ll_routeaddress.setVisibility(View.GONE);




        tvidellocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_addressVerificationContent.getVisibility() == View.VISIBLE) {
                    ll_addressVerificationContent.setVisibility(View.GONE);
                } else {
                    ll_addressVerificationContent.setVisibility(View.VISIBLE);
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this , ReportListActivity.class));
                finish();
            }
        });

       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTheReportOfParctularFromServer();
                getTheLatandLong();

            }
        });*/

    }

    private void toMap() {

        if (ll_mapaddress.getVisibility() == View.VISIBLE) {
           // ll_mapaddress.setVisibility(View.GONE);
            img_maprightt.setImageResource(R.drawable.ic_right);
        } else {
            ll_mapaddress.setVisibility(View.VISIBLE);
            img_maprightt.setImageResource(R.drawable.ic_down);

        }

        if(mMap != null){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0),15.0F));
                }
            },500);
        }


    }

    private void toRoute() {
        if (ll_routeaddress.getVisibility() == View.VISIBLE) {
            ll_routeaddress.setVisibility(View.GONE);
            img_routeright.setImageResource(R.drawable.ic_right);
        } else {
            ll_routeaddress.setVisibility(View.VISIBLE);
            img_routeright.setImageResource(R.drawable.ic_down);

        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_mapaddress:
                toMap();
                break;
            case R.id.linear_routeaddresss:
                toRoute();
                break;

        }

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap != null){
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
//        mMap.addMarker(new MarkerOptions().position(locationArrayList.get(0)).title("Marker"));
//
//
//        mMap.addMarker(new MarkerOptions().position(locationArrayList.get(locationArrayList.size() - 1)).title("Marker"));
////        for (int i = 0; i < locationArrayList.size(); i++) {
//
//            // mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList.get(i),10.0F));
//
//             locationArrayList.get(0);
//             mMap.addMarker(new MarkerOptions().position(locationArrayList.get(0)));
//
//        }

}

    private void getTheLatandLong() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", newUserId);
            jsonObject.put("date", newDate);
            jsonObject.put("start_time", startTime);
            jsonObject.put("end_time", endTime);

         /*   jsonObject.put("user_id", "4");
            jsonObject.put("date", "2022-02-07");
            jsonObject.put("start_time", "10:00");
            jsonObject.put("end_time", "18:30");*/

          /*  jsonObject.put("user_id", newUserId);
            jsonObject.put("date", newDate);
            jsonObject.put("start_time", startTime);
            jsonObject.put("end_time", endTime);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
       /* progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
        Log.d(Constants.LOG, jsonObject.toString());
        Log.d(Constants.LOG, Constants.GETCUSTOMER_MAP);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.GETCUSTOMER_MAP, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.LOG, response.toString());
                        progressDialog.dismiss();

                        try {
                            int status = response.getInt("status");
                            if (status == 1) {
                                parseTheJSONAndDisplayTheResultss(response);
                            } else if (status == 0) {
                                String message = "No Data Found";
                                anim_nodatafound.setVisibility(View.VISIBLE);
                                anim_loading.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());


                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                        AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", getSupportFragmentManager());

                        return;

                    }
                }
                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());
                anim_nodatafound.setVisibility(View.VISIBLE);
                anim_loading.setVisibility(View.GONE);
                //swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }
        };


        PlanetWorkVolleySingleton.getInstance(this).addToRequestQueue(mRequest);

    }

    private void parseTheJSONAndDisplayTheResultss(JSONObject response) {


        latLngList = new ArrayList<>();

        try {

            /*JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("data");*/
           JSONArray jsonArray = response.getJSONArray("data");

           if(jsonArray.length()>0) {

               for (int i = 0; i < jsonArray.length(); i++) {

                   JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                   latLngList.add(new LatLng(categoryJSONObject.getDouble("latitude"), categoryJSONObject.getDouble("longitude")));

               }
               Log.w("TAG", "lat array " + latLngList);

               if (mMap != null && latLngList.size() > 0) {
                   PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                   for (int z = 0; z < latLngList.size(); z++) {
                       LatLng point = latLngList.get(z);
                       options.add(point);
                   }
                   calculateDistance(latLngList);
                   // mMap.addPolyline(options);

                   mMap.addMarker(new MarkerOptions().position(latLngList.get(0)).title("Start")
                           .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_location_blue)));

                   mMap.addMarker(new MarkerOptions().position(latLngList.get(latLngList.size() - 1))
                           .title("End")
                           .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_location_red)));

                   MapAnimator.getInstance().animateRoute(mMap, latLngList);

                   rlrootmap.setVisibility(View.VISIBLE);
                   anim_loading.setVisibility(View.GONE);
                   anim_nodatafound.setVisibility(View.GONE);
                   //swipeRefreshLayout.setRefreshing(false);

                   if(mMap != null){
                       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0),15.0F));
                   }

               } else {
                   anim_loading.setVisibility(View.GONE);
                   anim_nodatafound.setVisibility(View.VISIBLE);
                   //   swipeRefreshLayout.setRefreshing(false);

               }
           }
           else {
               rlrootmap.setVisibility(View.GONE);
               anim_loading.setVisibility(View.GONE);
               anim_nodatafound.setVisibility(View.VISIBLE);
           }


        } catch (Exception e) {

        }

    }

    private void calculateDistance(List<LatLng> points) {

       /* float tempTotalDistance = 0;

        for (int i =0; i < points.size() -1; i++) {
            LatLng pointA =  points.get(i);
            LatLng pointB = points.get(i + 1);
            float[] results = new float[3];
            Location.distanceBetween (pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude, results);
            tempTotalDistance +=  results[0];
        }

        float totalD = tempTotalDistance;*/

        float totalDistance = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            Location tLoc1 = new Location("");
            Location tLoc2 = new Location("");

            tLoc1.setLatitude(points.get(i).latitude);
            tLoc1.setLongitude(points.get(i).longitude);

            tLoc2.setLatitude(points.get(i + 1).latitude);
            tLoc2.setLongitude(points.get(i + 1).longitude);

            totalDistance += tLoc1.distanceTo(tLoc2);
        }
        Log.v("TAG","tempTotalDistance "+totalDistance);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;
        final LinearInterpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}


