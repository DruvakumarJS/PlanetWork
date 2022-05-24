package com.netiapps.planetwork;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> latLngList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    LatLng sydney = new LatLng(13.018265, 77.647764);
    LatLng TamWorth = new LatLng(13.018647, 77.647329);
    LatLng NewCastle = new LatLng(13.019909, 77.647361);
    LatLng Brisbane = new LatLng(13.020667, 77.647379);




//     new LatLng(13.031975, 77.630050),
//                        new LatLng(13.018284, 77.647830),
//                        new LatLng(13.019455, 77.646843),
//                        new LatLng(13.020667, 77.647379),
//                        new LatLng(13.019454,  77.646843),
//                        new LatLng(13.019485, 77.645427)));

    // creating array list for adding all our locations.
    private ArrayList<LatLng> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = (SharedPreferences) getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        // in below line we are initializing our array list.
        locationArrayList = new ArrayList<>();


        getTheLatandLong();

        // on below line we are adding our
        // locations in our array list.
        locationArrayList.add(sydney);
        locationArrayList.add(TamWorth);
        locationArrayList.add(NewCastle);
        locationArrayList.add(Brisbane);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // inside on map ready method
        // we will be displaying all our markers.
        // for adding markers we are running for loop and
        // inside that we are drawing marker on our map.
        for (int i = 0; i < locationArrayList.size(); i++) {

            // below line is use to add marker to each location of our array list.
            mMap.addMarker(new MarkerOptions().position(locationArrayList.get(i)).title("Marker"));

            // below lin is use to zoom our camera on map.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f));

            // below line is use to move our camera to the specific location.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList.get(i)));

        }

    }


    private void getTheLatandLong() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", "2");
            jsonObject.put("date", "2021-10-25");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
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
                                parseTheJSONAndDisplayTheResults(response);
                            } else if (status == 0) {
                                String message = "No Data Found";

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
                   // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());


                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                       // AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", getSupportFragmentManager());

                        return;

                    }
                }
               // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + "6|T0Fg3pz2FGldaIDHx7Ps0pi66jftIiKIRQdDhSg0");//sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));
//
                return mHeaders;
            }
        };


        PlanetWorkVolleySingleton.getInstance(this).addToRequestQueue(mRequest);

    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {


        latLngList = new ArrayList<>();

        try {

            JSONArray jsonArray = response.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                latLngList.add(new LatLng(categoryJSONObject.getDouble("latitude"),categoryJSONObject.getDouble("longitude")));

            }

            if(mMap != null && latLngList.size() > 0){
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                for (int z = 0; z < latLngList.size(); z++) {
                    LatLng point = latLngList.get(z);
                    options.add(point);
                }
                mMap.addPolyline(options);
            }


        } catch (Exception e) {

        }

    }




}