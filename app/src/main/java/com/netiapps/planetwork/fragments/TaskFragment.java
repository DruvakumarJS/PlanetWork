package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.adapter.AdapterMyTasks;
import com.netiapps.planetwork.model.Home;
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

public class TaskFragment extends Fragment implements View.OnClickListener{

    private TextView header;
    private CardView cardbackbtn;
    private CircleImageView ivfilter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    String userID;
    private List<Home> myTasklist;
    AdapterMyTasks adapterMyTasks;

    SwipeRefreshLayout mSwipeRefreshLayout;
    private LottieAnimationView anim_nodatfound,anim_loading;
    private ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView rvmytasklist;
    public TaskFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.task_fragment_layout, container, false);

        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        userID = sharedPreferences.getString(Constants.userIdKey, "");

      /*  mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        imgBcak = view.findViewById(R.id.iv_back);
        imgBcak.setOnClickListener(this);*/

        cardbackbtn=view.findViewById(R.id.cardimage);
        header=view.findViewById(R.id.tvheader);
        rvmytasklist=view.findViewById(R.id.rvtasklisk);
        mSwipeRefreshLayout=view.findViewById(R.id.swipecontainer);
        ivfilter=view.findViewById(R.id.ivcircleimg);
        anim_nodatfound=view.findViewById(R.id.ivnodatafound);
        anim_loading=view.findViewById(R.id.loading);
        shimmerFrameLayout=view.findViewById(R.id.shimmerLayout);

        shimmerFrameLayout.startShimmer();

        //Picasso.get().load(R.drawable.ic_arrow).into(ivfilter);
       // ivfilter.setVisibility(View.VISIBLE);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myTasklist.clear();
              //  anim_loading.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                if(LocalHelper.isConnectedToInternet(getActivity())) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getAllTaskDataFromServer();
                        }
                    },3000);

                }
                else {
                    anim_loading.setVisibility(View.GONE);
                    anim_nodatfound.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

                }
            }
        });

        cardbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        header.setText("Task");
        myTasklist=new ArrayList<>();

      if(LocalHelper.isConnectedToInternet(getActivity())) {
          Handler handler=new Handler();
          handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                  getAllTaskDataFromServer();
              }
          },3000);

      }
      else {
         /* anim_loading.setVisibility(View.GONE);
          anim_nodatfound.setVisibility(View.VISIBLE);
          mSwipeRefreshLayout.setRefreshing(false);*/
          Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

      }

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onClick(View view) {

       /* switch (view.getId()){
            case R.id.ivback:

                break;
        }
*/
    }

    private void getAllTaskDataFromServer() {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
*/
        Log.d(Constants.LOG, Constants.JOBS);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.JOBS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
              //  progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        parseTheJSONAndDisplayTheResults(response);
                    } else if (status == 0) {
                        String message = "No Jobs Found for the Day";
                        mSwipeRefreshLayout.setRefreshing(false);
                        anim_loading.setVisibility(View.GONE);
                        anim_nodatfound.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                    return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.VISIBLE);
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
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);


    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {

        try {

            JSONArray jsonArray = response.getJSONArray("jobs");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                Home home = new Home();
                home.setJob_assign_id(categoryJSONObject.getString("job_assign_id"));
                home.setJob_date(categoryJSONObject.getString("job_date"));
                home.setCustomer_name(categoryJSONObject.getString("customer_name"));
                home.setBranch(categoryJSONObject.getString("branch"));
                home.setAddress(categoryJSONObject.getString("address"));
                home.setLatitude(categoryJSONObject.getString("latitude"));
                home.setLongitude(categoryJSONObject.getString("longitude"));
                home.setCity(categoryJSONObject.getString("city"));
                home.setJob_status(categoryJSONObject.getString("status"));
                home.setNo_of_visit(categoryJSONObject.getString("no_of_visit"));
                home.setJob_id(categoryJSONObject.getString("job_id"));
                home.setTask("Network Issue");
                home.setTask_status(categoryJSONObject.getString("status"));
                home.setSr_no(categoryJSONObject.getString("sr_no"));


                myTasklist.add(home);

            }


        } catch (Exception e) {

        }

        if (myTasklist.size() > 0) {

            adapterMyTasks= new AdapterMyTasks(getActivity(), myTasklist);
            rvmytasklist.setHasFixedSize(true);
            rvmytasklist.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rvmytasklist.setAdapter(adapterMyTasks);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
            rvmytasklist.setVisibility(View.VISIBLE);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.GONE);


        } else {
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);
           // Toast.makeText(getActivity(), "NO Jobs Found For the Day", Toast.LENGTH_SHORT).show();
        }
    }




}
