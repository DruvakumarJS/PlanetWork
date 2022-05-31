package com.netiapps.planetwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.adapter.AdapterAttendence;
import com.netiapps.planetwork.model.AttendenceModel;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AttendenceActivity extends AppCompatActivity {
    TextInputEditText tiTotalhours , tiActulahours , tiOvertime;
    TextView tvMonth;
    ImageView ivPrevious , ivNext ,ivinfo;
    RecyclerView rvAttendenceList;
    AdapterAttendence adapterAttendence;
    List<AttendenceModel> attendecelist=new ArrayList<>();
    SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context context;
    Calendar c;
    DateFormat df,year_df,month_df , datformat , monthname;
    int incremental =0;
    String requested_year , requested_month;
    private LottieAnimationView anim_loading, anim_norecord;
    LinearLayout ll_attendence ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_attendence);

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        c = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          //  df=new SimpleDateFormat("yyyy-MM-dd");
            df=new SimpleDateFormat("LLL yyyy");
            year_df=new SimpleDateFormat("yyyy");
            month_df=new SimpleDateFormat("MM");
            monthname=new SimpleDateFormat("LLL dd");
            datformat=new SimpleDateFormat("dd-MM-yyyy");

        }
        else {
            df=new SimpleDateFormat("LLL yyyy");
            year_df=new SimpleDateFormat("yyyy");
            month_df=new SimpleDateFormat("MM");
            monthname=new SimpleDateFormat("LLL dd");
            datformat=new SimpleDateFormat("dd-MM-yyyy");
        }

        tiTotalhours=findViewById(R.id.tvtotalhours);
        tiActulahours=findViewById(R.id.tvactualhours);
        tiOvertime=findViewById(R.id.tvovertime);
        rvAttendenceList=findViewById(R.id.rvattendencelist);
        ivPrevious=findViewById(R.id.ivlastmonth);
        ivNext=findViewById(R.id.ivnextmonth);
        tvMonth=findViewById(R.id.tvmonthname);
        anim_norecord=findViewById(R.id.nodata);
        anim_loading=findViewById(R.id.loading);
        ivinfo=findViewById(R.id.ivinfo);

        tiTotalhours.setText("0 hrs");
        tiActulahours.setText("0 hrs");
        tiOvertime.setText("+0 hrs");

        tiActulahours.setFocusable(false);
        tiTotalhours.setFocusable(false);
        tiOvertime.setFocusable(false);
        tvMonth.setText(df.format(c.getTime()));
        requested_year=year_df.format(c.getTime());
        requested_month=month_df.format(c.getTime());

        rvAttendenceList.setLayoutManager(new LinearLayoutManager(AttendenceActivity.this,
                LinearLayoutManager.VERTICAL, false));
      getattendance(requested_year , requested_month);
       //displayAttedencereport(response);
        rvAttendenceList.setItemAnimator(new DefaultItemAnimator());

        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateprevoiusmonth();
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatenextmonth();

            }
        });

        ivinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showalert();
            }
        });

    }

    private void populateprevoiusmonth() {
        incremental = incremental -1;
        Calendar cal=Calendar.getInstance();
        if(incremental !=0)
        {
            cal.add(Calendar.MONTH, incremental);
        }

        tvMonth.setText(df.format(cal.getTime()));
        requested_year=year_df.format(cal.getTime());
        requested_month=month_df.format(cal.getTime());
        Log.v("TAG","requestedate "+requested_year +"-"+requested_month);

        getattendance(requested_year , requested_month);
    }


    private void populatenextmonth() {
        Calendar cal=Calendar.getInstance();
        incremental = incremental +1;
        if(incremental !=0)
        {
            cal.add(Calendar.MONTH, incremental);
        }

        tvMonth.setText(df.format(cal.getTime()));
        /* requestedate=numer_df.format(cal.getTime());
        Log.v("TAG","requestedate "+requestedate);*/
        requested_year=year_df.format(cal.getTime());
        requested_month=month_df.format(cal.getTime());
        Log.v("TAG","requestedate "+requested_year +"-"+requested_month);
        getattendance(requested_year , requested_month);
  }

    private void getattendance(String year, String month) {

        anim_loading.setVisibility(View.VISIBLE);
        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, mSharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("year",year);
        listDetails.put("month",month);

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().attendencereport(mHeaders, listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if(attendecelist!=null)
                    {
                        attendecelist.clear();
                    }

                    String result = new Gson().toJson(response.body());
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        displayAttedencereport(result);

                    }
                    else{
                        tiTotalhours.setText("0" + " hrs");
                        tiActulahours.setText("0"  + " hrs");
                        tiOvertime.setText("0"  + " hrs");

                        rvAttendenceList.setVisibility(View.GONE);
                        anim_loading.setVisibility(View.GONE);
                        anim_norecord.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    anim_loading.setVisibility(View.GONE);
                    anim_norecord.setVisibility(View.VISIBLE);

                    tiTotalhours.setText("0" + " hrs");
                    tiActulahours.setText("0"  + " hrs");
                    tiOvertime.setText("0"  + " hrs");

                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("TAG", "Retrofit error " + t.getMessage());

                anim_loading.setVisibility(View.GONE);
                anim_norecord.setVisibility(View.VISIBLE);

                tiTotalhours.setText("0" + " hrs");
                tiActulahours.setText("0"  + " hrs");
                tiOvertime.setText("0"  + " hrs");

            }
        });

    }

    private void displayAttedencereport(String result) {
        try {
            JSONObject jsonObject=new JSONObject(result);

            String status=jsonObject.getString("status");
                int total = Integer.parseInt(jsonObject.getString("total_hours"));
                int actual = Integer.parseInt(jsonObject.getString("actual_hours"));

                int ot_min=actual-total;
                int NewString = Integer.parseInt(String.valueOf(ot_min).replaceAll("-", ""));

                String formated_hr=((total/60)<10)?"0"+Integer.toString((total/60)):Integer.toString((total/60));
                String formated_min=((total%60)<10)?"0"+Integer.toString((total%60)):Integer.toString((total%60));
                String formated_ahr=((actual/60)<10)?"0"+Integer.toString((actual/60)):Integer.toString((actual/60));
                String formated_amin=((actual%60)<10)?"0"+Integer.toString((actual%60)):Integer.toString((actual%60));
                String formated_oth=((NewString/60)<10)?"0"+Integer.toString((NewString/60)):Integer.toString((NewString/60));
                String formated_otmin=((NewString%60)<10)?"0"+Integer.toString((NewString%60)):Integer.toString((NewString%60));

                tiTotalhours.setText(formated_hr+":"+formated_min);
                tiActulahours.setText(formated_ahr +":"+formated_amin);
                if(String.valueOf(ot_min).contains("-")) {
                    tiOvertime.setText("-"+formated_oth + ":" + formated_otmin);
                }
                else {
                    tiOvertime.setText(formated_oth + ":" + formated_otmin
                    );
                }

                JSONArray jsonArray = jsonObject.getJSONArray("report");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject attendence = jsonArray.getJSONObject(i);

                    String date = attendence.getString("date");
                    String login = attendence.getString("login");
                    String logout = attendence.getString("logout");
                    String minutes = attendence.getString("working_hours");
                    String is_holiday = attendence.getString("is_holiday");
                    String is_weekend = attendence.getString("is_weekend");
                    String onleave = attendence.getString("is_leave");
                    String dayname = attendence.getString("day");
                    String holiday_name=attendence.getString("holiday_title");
                  /*  String monthh="";
                    try {
                        Date date1=datformat.parse(date);
                         monthh=monthname.format(date1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
*/

                    AttendenceModel attendencedata = new AttendenceModel();
                    attendencedata.setDate(date);
                    attendencedata.setLogin(login);
                    attendencedata.setLogout(logout);
                    attendencedata.setWorking_hours(minutes);
                    attendencedata.setIs_holiday(is_holiday);
                    attendencedata.setIs_weekend(is_weekend);
                    attendencedata.setIs_leave(onleave);
                    attendencedata.setDay(dayname);
                    attendencedata.setHoliday_title(holiday_name);
                    attendecelist.add(attendencedata);

                }

                adapterAttendence = new AdapterAttendence(AttendenceActivity.this, attendecelist);
                rvAttendenceList.setAdapter(adapterAttendence);
                anim_loading.setVisibility(View.GONE);
                anim_norecord.setVisibility(View.GONE);
              //  ll_attendence.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showalert() {
        new MaterialAlertDialogBuilder(AttendenceActivity.this)
                .setTitle("PlanetWork")
                .setMessage("*Total hours indicates the number of hours you should work on current month." +
                        "\n*Actual hours indicates the total hours you worked on current month .\n" +
                        "*OverTime hours ,a - number indicates remaining hours to be worked on current month and + number indicates the extra hours or OT you have done   ")
                .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }
}