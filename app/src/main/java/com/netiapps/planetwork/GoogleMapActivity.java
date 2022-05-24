package com.netiapps.planetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.MapAnimator;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> latLngList;
    float totalDistance = 0;
    SharedPreferences sharedPreferences;
    String start_time , end_time , date;

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
        setContentView(R.layout.activity_google_map);

        sharedPreferences =  getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);

        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(GoogleMapActivity.this);

        Intent intent=getIntent();
        start_time=intent.getStringExtra("start_time");
        end_time=intent.getStringExtra("end_time");
        date=intent.getStringExtra("date");

        load_Map();

        getTheLatandLong();




    }

    private void getTheLatandLong() {
        String request_date=null;
        DateFormat datformat=new SimpleDateFormat("dd-MM-yyyy");
        DateFormat datformat2=new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1=datformat.parse(date);
            request_date=datformat2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("date",request_date);
        listDetails.put("start_time",start_time+":00");
        listDetails.put("end_time", end_time+":59");

//{"user_id":"2","date":"May 17","start_time":"10:49:00","end_time":"16:36:59"}
     /*   listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("date","2022-05-17");
        listDetails.put("start_time","10:49:00");
        listDetails.put("end_time", "16:36:59");*/

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().get_user_path(mHeaders, listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {

                    String result = new Gson().toJson(response.body());
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        DisplayMap(result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("TAG", "Retrofit error " + t.getMessage());
            }
        });

    }
    private void DisplayMap(String response) {

        latLngList = new ArrayList<>();
        try {

            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            if(jsonArray.length()>0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                    latLngList.add(new LatLng(categoryJSONObject.getDouble("latitude"), categoryJSONObject.getDouble("longitude")));

                }
                Log.w("TAG", "lat array " + latLngList);

                if (latLngList.size() > 0) {
                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                    for (int z = 0; z < latLngList.size(); z++) {
                        LatLng point = latLngList.get(z);
                        options.add(point);


                    }
                    calculateDistance(latLngList);
                    mMap.addMarker(new MarkerOptions().position(latLngList.get(0)).title("Start")
                            .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_location_blue)));

                    mMap.addMarker(new MarkerOptions().position(latLngList.get(latLngList.size() - 1))
                            .title("End")
                            .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_location_red)));

                    MapAnimator.getInstance().animateRoute(mMap, latLngList);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0),15.0F));


                    //swipeRefreshLayout.setRefreshing(false);

                } else {
                    Log.v("TAG","No lat-long");

                    //   swipeRefreshLayout.setRefreshing(false);

                }
            }
            else {
                Log.v("TAG","No JSOn");
            }


        } catch (Exception e) {

        }
    }

    private void load_Map() {

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null){
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }

       // getTheLatandLong();
       // DisplayMap(response);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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

    private void calculateDistance(List<LatLng> points) {

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

}