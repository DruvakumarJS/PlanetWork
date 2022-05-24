package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PieChartActivity  extends Fragment implements View.OnClickListener {
    TextView tvR, tvPython, tvCPP, tvJava;
    PieChart pieChart;
    SwipeRefreshLayout mSwipeRefreshLayout;
    JsonObject pieChartdata=new JsonObject();

    List<String> months=null;
    Spinner spinner;
    String date;
    RelativeLayout chartlayout;

    private TextView header;
    private CardView cardbackbtn;
    private CircleImageView cardivprofile;
    private  TextView tvfilter;
    SharedPreferences sharedPreferences;
    private LottieAnimationView anim_nodatfound,anim_loading;


    private String piechartdata="[\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2022-02\",\n" +
            "    \"earnings\":\"700\",\n" +
            "    \"t_allowance\":\"250\",\n" +
            "    \"leave_balance\":\"3\",\n" +
            "    \"paid_leave\":\"4\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2022-01\",\n" +
            "    \"earnings\":\"800\",\n" +
            "    \"t_allowance\":\"100\",\n" +
            "    \"leave_balance\":\"3\",\n" +
            "    \"paid_leave\":\"3\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2021-12\",\n" +
            "    \"earnings\":\"650\",\n" +
            "    \"t_allowance\":\"1000\",\n" +
            "    \"leave_balance\":\"5\",\n" +
            "    \"paid_leave\":\"2\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2021-11\",\n" +
            "    \"earnings\":\"850\",\n" +
            "    \"t_allowance\":\"685\",\n" +
            "    \"leave_balance\":\"5\",\n" +
            "    \"paid_leave\":\"7\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2021-10\",\n" +
            "    \"earnings\":\"975\",\n" +
            "    \"t_allowance\":\"444\",\n" +
            "    \"leave_balance\":\"4\",\n" +
            "    \"paid_leave\":\"6\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"user_id\":\"3\",\n" +
            "    \"date\":\"2021-08\",\n" +
            "    \"earnings\":\"225\",\n" +
            "    \"t_allowance\":\"500\",\n" +
            "    \"leave_balance\":\"3\",\n" +
            "    \"paid_leave\":\"5\"\n" +
            "  }\n" +
            "]";


    public PieChartActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.piechart, container, false);

        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);


        tvR = view.findViewById(R.id.tvR);
        tvPython = view.findViewById(R.id.tvPython);
        tvCPP = view.findViewById(R.id.tvCPP);
        tvJava = view.findViewById(R.id.tvJava);
        pieChart = view.findViewById(R.id.piechart);
      //  tvfilter=view.findViewById(R.id.tvfiletrmonth);
        spinner=view.findViewById(R.id.datespinner);

        cardbackbtn=view.findViewById(R.id.cardimage);
        header=view.findViewById(R.id.tvheader);
        cardivprofile=view.findViewById(R.id.ivcircleimg);

        header.setText("Pie Chart ");

        Picasso.get().load(R.drawable.ic_person).into(cardivprofile);

        mSwipeRefreshLayout=view.findViewById(R.id.swipecontainer);
        anim_nodatfound=view.findViewById(R.id.ivnodatafound);
        anim_loading=view.findViewById(R.id.loading);
        chartlayout=view.findViewById(R.id.rlchart);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(LocalHelper.isConnectedToInternet(getActivity())) {
                    getpiechartdata();
                }
              else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    chartlayout.setVisibility(View.GONE);
                    anim_loading.setVisibility(View.GONE);
                    anim_nodatfound.setVisibility(View.VISIBLE);
                   // Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

                }
            }
        });


        if(LocalHelper.isConnectedToInternet(getActivity())) {
            getpiechartdata();
        }
        else {

            mSwipeRefreshLayout.setRefreshing(false);
            chartlayout.setVisibility(View.GONE);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);
          //  Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Log.v("TAG","data is "+spinner.getSelectedItem().toString());

                String selecteddate=spinner.getSelectedItem().toString();

                pieChart.clearChart();
                loadchart(selecteddate);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    private void loadchart(String selecteddate) {
        try {
            JSONArray jsonArray=new JSONArray(piechartdata);

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                date=selecteddate;

                        if(jsonObject.getString("date").equalsIgnoreCase(date)) {
                            String t_allowance = jsonObject.getString("t_allowance");
                            String earnings = jsonObject.getString("earnings");
                            String l_balance = jsonObject.getString("leave_balance");
                            String paid_leave=jsonObject.getString("paid_leave");

                            setData(earnings, t_allowance, l_balance, paid_leave);
                        }

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

        }

    }


    private void setData(String earnings, String t_allowance, String l_balance, String paid_leave)
    {

      /*  tvR.setText(Integer.toString(40));
        tvPython.setText(Integer.toString(30));
        tvCPP.setText(Integer.toString(5));
        tvJava.setText(Integer.toString(25));*/

        tvR.setText(earnings);
        tvPython.setText(t_allowance);
        tvCPP.setText(l_balance);
        tvJava.setText(paid_leave);


        pieChart.addPieSlice(
                new PieModel(
                        "R",
                        Integer.parseInt(tvR.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Python",
                        Integer.parseInt(tvPython.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "C++",
                        Integer.parseInt(tvCPP.getText().toString()),
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Java",
                        Integer.parseInt(tvJava.getText().toString()),
                        Color.parseColor("#29B6F6")));

        // To animate the pie chart
        pieChart.startAnimation();
        chartlayout.setVisibility(View.VISIBLE);
        anim_loading.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getpiechartdata() {

        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));


        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));


       // LocaleHelper.showProgressDialog(this, getString(R.string.please));
      /*  Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getchartdata(mHeaders,listDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               *//* if (pieChartdata.size() > 0) {
                    pieChartdata.clear();
                }
                pieChartdata = (JsonObject) response.body();
*//*
               // Log.v("TAG", "JOB response is "+response.body().toString());

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });*/

        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getchartdata(mHeaders,listDetails);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               /* if (pieChartdata.size() > 0) {
                    pieChartdata.clear();
                }
                pieChartdata = (JsonObject) response.body();
*/
               /* pieChartdata = response.body();
                Log.v("TAG","pieChartdata"+pieChartdata);*/

                months=new ArrayList<>();
                ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, months);


                try {
                    JSONArray jsonArray=new JSONArray(piechartdata);

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        date=jsonObject.getString("date");
                        months.add(date);
                    }

                    spinner.setAdapter(adapterTime);
                    chartlayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.VISIBLE);

            }
        });

    }

}
