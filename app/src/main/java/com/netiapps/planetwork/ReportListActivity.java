package com.netiapps.planetwork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.netiapps.planetwork.adapter.AdapterTravelHistory;
import com.netiapps.planetwork.model.ReportModel;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportListActivity extends AppCompatActivity {

    private CardView cardimgBcak;
    private  TextView tvheader ,tvfilter;
    private CircleImageView ivfilter;
    private RecyclerView recyclerView;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userId;
    private List<ReportModel> myreportListt;
    private AdapterTravelHistory travelHistorylist;
    private  CardView cardfilterview;
    private  String filtereditem;

    private LottieAnimationView ivNodatafound;
    private LottieAnimationView animloading;
    private  String SelctedDate;
    private  String todayDate;
    SwipeRefreshLayout swipeRefreshLayout;
    SimpleDateFormat sdf;
    String startingDay , endingDay;
    private ShimmerFrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlist);

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        todayDate= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        sdf = new SimpleDateFormat("yyyy-MM-dd");

        userId = mSharedPreferences.getString(Constants.userIdKey, "");

        cardimgBcak = findViewById(R.id.cardimage);
        tvheader=findViewById(R.id.tvheader);
        recyclerView = findViewById(R.id.recyclerView);
        ivfilter=findViewById(R.id.ivcircleimg);
        ivNodatafound=findViewById(R.id.ivnodatafound);
        animloading=findViewById(R.id.loading);
        cardfilterview=findViewById(R.id.filtercard);
        tvfilter=findViewById(R.id.tvfilter);
        swipeRefreshLayout=findViewById(R.id.swipelayout);
        mFrameLayout = findViewById(R.id.shimmerLayout);
        mFrameLayout.startShimmer();

        tvheader.setText("My Reports");
        ivfilter.setVisibility(View.VISIBLE);

        myreportListt = new ArrayList<>();

        startingDay=todayDate;
        endingDay=startingDay;

        mFrameLayout.startShimmer();
        mFrameLayout.setVisibility(View.VISIBLE);

        getTheReportFromServer(startingDay,endingDay);



        cardimgBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

               getTheReportFromServer(startingDay,endingDay);
            }
        });

        ivfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReportListActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        cardfilterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ReportListActivity.this, ivfilter);
                popup.getMenuInflater().inflate(R.menu.report_filter_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        // tvfilter.setText(item.getTitle());
                        recyclerView.setVisibility(View.GONE);
                        mFrameLayout.startShimmer();
                        mFrameLayout.setVisibility(View.VISIBLE);

                        if(item.getItemId()==R.id.dummy){
                            Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else  if(item.getItemId()==R.id.today)
                        {
                            tvfilter.setText(item.getTitle());
                            Log.v("TAG","today -->"+todayDate +","+todayDate);
                            startingDay=todayDate;
                            endingDay=startingDay;
                            getTheReportFromServer(startingDay,endingDay);

                           // Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else  if(item.getItemId()==R.id.week)
                        {
                            tvfilter.setText(item.getTitle());

                            Calendar cal = Calendar.getInstance();
                            try{
                                cal.setTime(sdf.parse(todayDate));
                            }catch(ParseException e){
                                e.printStackTrace();
                            }
                            cal.add(Calendar.DAY_OF_MONTH, -7);
                            String previous_week = sdf.format(cal.getTime());

                            Log.v("TAG","week -->"+previous_week +","+todayDate);

                            startingDay=previous_week;
                            endingDay=todayDate;
                            getTheReportFromServer(startingDay,endingDay);
                           // getTheReportFromServer(previous_week,todayDate);
                           // Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();

                        }
                        else  if(item.getItemId()==R.id.month)
                        {
                            tvfilter.setText(item.getTitle());

                            Calendar cal = Calendar.getInstance();
                            try{
                                cal.setTime(sdf.parse(todayDate));
                            }catch(ParseException e){
                                e.printStackTrace();
                            }
                            cal.add(Calendar.DAY_OF_MONTH, -30);
                            String previous_month = sdf.format(cal.getTime());

                            Log.v("TAG","month -->"+previous_month +","+todayDate);
                            startingDay=previous_month;
                            endingDay=todayDate;
                            getTheReportFromServer(startingDay,endingDay);
                            //getTheReportFromServer(previous_month,todayDate);
                          //  Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else if(item.getItemId()==R.id.custome)
                        {
                            opencalender();
                          //  Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();

                        }
                        else if(item.getItemId()==R.id.customerange)
                        {
                            openRangeselector();

                        }
                      /*  else {
                            tvfilter.setText(item.getTitle());
                            getTheReportFromServer(item.getTitle().toString(),"");
                            Toast.makeText(ReportListActivity.this,"You Clicked : " + tvfilter.getText().toString(), Toast.LENGTH_SHORT).show();

                        }*/
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

    }

    private void openRangeselector() {
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());

        final MaterialDatePicker.Builder<Pair<Long, Long>> materialDatePickerBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDatePickerBuilder.setTitleText("SELECT A DATE");
        materialDatePickerBuilder.setTheme(R.style.Theme_Planetwork_DatePicker);
        materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build());
        final MaterialDatePicker<Pair<Long, Long>> materialDatePicker = materialDatePickerBuilder.build();

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                Long startDate = selection.first;
                Long endDate = selection.second;

                tvfilter.setText(materialDatePicker.getHeaderText());
                filtereditem=tvfilter.getText().toString().trim();

                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date1 = new Date((Long) selection.first);
                Date date2 = new Date((Long) selection.second);

                Log.v("TAG","custome range -->"+sdf.format(date1) +","+sdf.format(date2));

                startingDay=sdf.format(date1);
                endingDay=sdf.format(date2);
                getTheReportFromServer(startingDay,endingDay);
               // getTheReportFromServer(sdf.format(date1),sdf.format(date2));


            }
        });

    }

    private void opencalender() {
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());

        final MaterialDatePicker.Builder<Long> materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker();
        materialDatePickerBuilder.setTitleText("SELECT A DATE");
        materialDatePickerBuilder.setTheme(R.style.Theme_Planetwork_DatePicker);
        materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build());
        final MaterialDatePicker<Long> materialDatePicker = materialDatePickerBuilder.build();

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        tvfilter.setText(materialDatePicker.getHeaderText());

                        filtereditem=tvfilter.getText().toString().trim();

                        TimeZone timeZoneUTC = TimeZone.getDefault();

                        int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;

                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date date = new Date((Long) selection);

                       // tvfilter.setText(simpleFormat.format(date));

                        startingDay=sdf.format(date);
                        endingDay=sdf.format(date);
                        getTheReportFromServer(startingDay,endingDay);
                     //  getTheReportFromServer(sdf.format(date),sdf.format(date));

                        Log.v("TAG","custome day -->"+sdf.format(date) +","+sdf.format(date));
                    }
                });

    }

    private void getTheReportFromServer(String startday,String enddate) {

        if(LocalHelper.isConnectedToInternet(ReportListActivity.this)) {
            ivNodatafound.setVisibility(View.GONE);


            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", mSharedPreferences.getString(Constants.userIdKey, ""));
                //jsonObject.put("user_id", "3");
                jsonObject.put("from_date", startday);
                jsonObject.put("to_date", enddate);

            } catch (JSONException e) {
                e.printStackTrace();
            }
     /*   final ProgressDialog progressDialog = new ProgressDialog(this);
       progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/

            Log.d(Constants.LOG, Constants.GETTRIP_DETAILS);
            JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.GETTRIP_DETAILS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // Log.d(Constants.LOG, "work report api list : "+response.toString());
                    //  progressDialog.dismiss();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            parseTheJSONAndDisplayTheResults(response);
                        } else if (status == 0) {
                            String message = "No Leave Found";

                            ivNodatafound.setVisibility(View.VISIBLE);
                            mFrameLayout.stopShimmer();
                            mFrameLayout.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // progressDialog.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        //  AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                        ivNodatafound.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
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
                    mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                    return mHeaders;
                }

            };

            mRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            PlanetWorkVolleySingleton.getInstance(ReportListActivity.this).addToRequestQueue(mRequest);

        }

        else
        {
            ivNodatafound.setVisibility(View.VISIBLE);
            animloading.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            mFrameLayout.stopShimmer();
            mFrameLayout.setVisibility(View.GONE);
            Toast.makeText(ReportListActivity.this,"No Connection Availabale",Toast.LENGTH_SHORT).show();
        }

    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {

        if(myreportListt!=null){
            myreportListt.clear();
        }

        try {

            JSONArray jsonArray = response.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                ReportModel reportModel = new ReportModel();
                reportModel.setDate(categoryJSONObject.getString("date"));
                reportModel.setStart_time(categoryJSONObject.getString("start"));
                reportModel.setEnd_time(categoryJSONObject.getString("end"));
                reportModel.setFrom_address(categoryJSONObject.getString("from_address"));
                reportModel.setTo_address(categoryJSONObject.getString("to_address"));
                reportModel.setDistance(categoryJSONObject.getString("travel_distance"));
                reportModel.setSrno(categoryJSONObject.getString("sr_no"));

                myreportListt.add(reportModel);

            }


        } catch (Exception e) {

        }

        if (myreportListt.size() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    travelHistorylist = new AdapterTravelHistory(ReportListActivity.this, myreportListt);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(travelHistorylist);
                    recyclerView.setVisibility(View.VISIBLE);
                    ivNodatafound.setVisibility(View.GONE);
                    mFrameLayout.stopShimmer();
                    mFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            },2000);


        } else {
            ivNodatafound.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            mFrameLayout.stopShimmer();
            mFrameLayout.setVisibility(View.GONE);
           // Toast.makeText(ReportListActivity.this, "NO Report Found", Toast.LENGTH_SHORT).show();
        }

    }

  /*  public class MyConsignmnetAdapter extends RecyclerView.Adapter<MyConsignmnetAdapter.ViewHolder> {

        private List<ReportModel> inputList = new ArrayList<>();
        private List<ReportModel> itemList = new ArrayList<>();
        private Context context;

        public MyConsignmnetAdapter(Context context, List<ReportModel> items) {
            if (items != null && !items.isEmpty()) {
                itemList = items;
                inputList = items;
            }
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.reportsinglelist, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ReportModel reportModel = itemList.get(position);


            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle b = msg.getData();
                    String value = b.getString("address");

                    String addresType = b.getString("address_type");

                    if(addresType.equalsIgnoreCase("from")){
                        holder.tvaddress.setText("From:" + " " + value);
                    }else{
                        holder.tvtoaddress.setText("To:" + " " + value);
                    }
                }
            };

            getAddressFromLocation(Double.parseDouble(reportModel.getFrom_lat()) , Double.parseDouble(reportModel.getFrom_lng()),context,handler,"from");
            getAddressFromLocation(Double.parseDouble(reportModel.getTo_lat()) , Double.parseDouble(reportModel.getTo_lng()),context,handler,"to");
            holder.tvDate.setText("Date :" + " " +reportModel.getDate());
            holder.tvTime.setText("StartTime :" + " " +reportModel.getStart_time());
            holder.tvendTime.setText("EndTime : " + " " + reportModel.getEnd_time());
            holder.tvdistance.setText(reportModel.getDistance());

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReportListActivity.this,ReportActivity.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("Date",reportModel.getDate());
                    startActivity(intent);
                }
            });

        }


        @Override
        public int getItemCount() {
            if (itemList == null) {
                return 0;
            } else {
                return itemList.size();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate;
            TextView tvdistance;
            TextView tvaddress;
            TextView tvTime;
            TextView tvendTime;
            TextView tvtoaddress;
          //  TextView tvitldayscompnay;
            LinearLayout linearLayout;
            View itemView;


            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvaddress = (TextView) itemView.findViewById(R.id.tvaddress);
                tvdistance = (TextView) itemView.findViewById(R.id.tvdistance);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                linearLayout = itemView.findViewById(R.id.llinerlayout);
                tvendTime = (TextView) itemView.findViewById(R.id.tvendTime);
                tvtoaddress= (TextView) itemView.findViewById(R.id.tvtoaddress);
                //tvitldayscompnay = (TextView) itemView.findViewById(R.id.tvitldayscompnay);

            }
        }
    }
*/
    /*public void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler,final String addresType) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(ReportListActivity.this, Locale.getDefault());
                String result = null;
                try {
                    List <Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);

                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getAddressLine(0));

                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        bundle.putString("address_type", addresType);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address for this location.";
                        bundle.putString("address", result);
                        bundle.putString("address_type", addresType);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }*/

}
