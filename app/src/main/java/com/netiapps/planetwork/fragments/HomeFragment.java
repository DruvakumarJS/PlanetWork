package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.ebanx.swipebtn.SwipeButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.ProfileActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.adapter.AdapterHomeTasks;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.locationbackground.GetLocation_Service;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.model.TasksDAO;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.roomdatabase.LatlangDatabse;
import com.netiapps.planetwork.roomdatabase.LatlangEntity;
import com.netiapps.planetwork.roomdatabase.latlangDAO;
import com.netiapps.planetwork.services.GpsTracker;
import com.netiapps.planetwork.services.LoginTimeCounterService;
import com.netiapps.planetwork.swipe.ProSwipeButton;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final double EARTH_RADIUS = 6371000;
    private Toolbar mToolbar;
    private ImageView ivHamMenu;
    private OnFragmentInteractionListener mListener;
    SwipeButton enableButton;
    private ImageView imgPriofile;
    private LinearLayout llcabling;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView tvStatus;
    ProSwipeButton proSwipeBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    String userName;
    String userID;
    String profilephoto;
    private TextView tvName;
    private RecyclerView recyclerViewList;
    private List<TasksDAO> myHomeListt;

    private List<String>mytaskstatuslist=new ArrayList<>();
  //  private MyConsignmnetAdapter myConsignmnetAdapter;
    private AdapterHomeTasks adapterHomeTasks;

    private List<TextView> listdateText;

    private List<TextView> listcompanyText;

    private List<TextView> listbranchText;
    private List<TextView> liststatus;

    private  int FIVE_SECONDS = 5000;
    Handler handler = new Handler();
    Handler locationhandler = new Handler();

    Runnable locationrunnable;
    double latitude;
    double longitude;
    private GpsTracker gpsTracker;
    private static final int DATA_SAVED_SUCCESSFULLY = 10;
    Context context;
    SwipeRefreshLayout mSwipeRefreshLayout;

    BroadcastReceiver broadcastReceiver;
    int index;
    private Parcelable recyclerViewState;
    ServiceConnection mConnection;
    GetLocation_Service mService;
    boolean mBound = false;
    private final String PACKAGE_URL_SCHEME = "package:";

    Intent mServiceIntent;

    private latlangDAO latlangDAO;

    private DbModelSendingData dbModelSendingData;
    private LottieAnimationView anim_nodatfound,anim_loading;
    Intent loginIntentService;
    SimpleDateFormat sdf;
    String format = "yyyy-MM-dd hh:mm:ss";
    ImageView activestatus;
    int logouthour=9*60*60;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imgPriofile = (ImageView) view.findViewById(R.id.iv_personProfile);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mListener = (OnFragmentInteractionListener) activity;
        context=getActivity();
        sdf = new SimpleDateFormat("yyyy-MM-dd");


        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        mEditor.putBoolean(Constants.REVERSE_FIRST_TIME, true);
      //  mEditor.putBoolean("workinprogress",false);
        mEditor.apply();

        userName = sharedPreferences.getString(Constants.userNameKey, "");
        userID = sharedPreferences.getString(Constants.userIdKey, "");
        profilephoto = sharedPreferences.getString(Constants.photo, "");

        tvName = view.findViewById(R.id.title);

        tvName.setText("Hi , " + userName );

        ivHamMenu = view.findViewById(R.id.iv_ham_menu);
        mSwipeRefreshLayout=view.findViewById(R.id.swipecontainer);
        imgPriofile = (ImageView) view.findViewById(R.id.iv_personProfile);

        imgPriofile.setBackgroundResource(R.drawable.ic_person);
        anim_nodatfound=view.findViewById(R.id.ivnodatafound);
        anim_loading=view.findViewById(R.id.loading);
        activestatus=view.findViewById(R.id.iv_active_status);

        recyclerViewList = view.findViewById(R.id.recyclerView);
        myHomeListt = new ArrayList<>();
        listdateText = new ArrayList<TextView>();
        listcompanyText = new ArrayList<TextView>();
        listbranchText = new ArrayList<TextView>();
        liststatus = new ArrayList<TextView>();

        ivHamMenu.setOnClickListener(this);
        imgPriofile.setOnClickListener(this);

       /* List<DbModelSendingData> locationData = getAllData();
        if(locationData.size() > 0) {
            Log.v("TAG","db count "+locationData.size());
            for(int i = 0; i < locationData.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                DbModelSendingData dbModelSendingData = locationData.get(0);
                String time=dbModelSendingData.getDate()+""+dbModelSendingData.getTime();
             //   Log.v("TAG","first row is "+time);

            }
        }*/

        String last_db_sync_time=sharedPreferences.getString("db_sync_time","");
        String currentDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date dateObj1 = sdf.parse(last_db_sync_time) ;
            Date dateObj2 = sdf.parse(currentDate);

            long diff = dateObj2.getTime() - dateObj1.getTime();
            double diffInHours = diff / ((double) 1000 * 60 * 60);
            double diffInMin = diff / ((double) 1000 * 60 );
            int minute=(int)diffInMin;

           // Log.v("TAG","last_db_sync_time " + last_db_sync_time);
           // Log.v("TAG","minute " + minute);

            if(minute>180)
            {
                activestatus.setBackgroundResource(R.drawable.active_red_foreground);
            }
            else if(minute>30 && minute<=180)
            {
                activestatus.setBackgroundResource(R.drawable.active_orange_foreground);
            }
            else if(minute<=30)
            {
                activestatus.setBackgroundResource(R.drawable.active_green_foreground);
            }
            else {
                activestatus.setBackgroundResource(R.drawable.active_red_foreground);
            }

        } catch (ParseException e) {
            activestatus.setVisibility(View.GONE);
            e.printStackTrace();

        }


        //scheduleSendLocation();
        if(LocalHelper.isConnectedToInternet(getActivity())) {
            anim_loading.setVisibility(View.VISIBLE);
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTheHomeDetailsFromServer();
                }
            },2000);

        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);

        }

         proSwipeBtn = view.findViewById(R.id.proswipebutton_main);

            proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        proSwipeBtn.showResultIcon(true, false);

                        if(LocalHelper.isConnectedToInternet(getActivity()))
                        {

                            if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("0"))
                            {
                                mEditor.putBoolean("insideGeofence",false);
                             //  Toast.makeText(getActivity(),"Geo fence removed",Toast.LENGTH_SHORT).show();
                                if (isMyServiceRunning(LoginTimeCounterService.class)) {
                                    Intent intent = new Intent(getActivity(), LoginTimeCounterService.class);
                                    context.stopService(intent);
                                }
                                else
                                {

                                }
                            }

                            mEditor.putBoolean("workinprogress",false);
                            mEditor.putString("job_id","0");
                            //mEditor.putBoolean("isresetTaskSelection",false);
                            mEditor.putBoolean("isresetTaskSelection",true);
                            mEditor.apply();
                        }

                    }
                }, 2000);
            }
        });

        proSwipeBtn.startStop(new ProSwipeButton.LoginStartStopListener() {
            @Override
            public void startStop(String status) {

               /* Log.v("TAG","session login stop: "+sharedPreferences.getString("session_loggedin","0"));
                Log.v("TAG","session login stop: "+sharedPreferences.getString("timer_paused","0"));

                if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1") && sharedPreferences.getString("timer_paused","0").equalsIgnoreCase("true"))
                {
                    Toast.makeText(getActivity(), "logout", Toast.LENGTH_SHORT).show();
                  //  call_attendednce_api("logout");
                }
                else  if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1") && sharedPreferences.getString("timer_paused","0").equalsIgnoreCase("false")) {
                  //  call_attendednce_api("login");
                    Toast.makeText(getActivity(), "login", Toast.LENGTH_SHORT).show();
                }
*/
                    //   call_attendednce_api("logout");
                mEditor.putBoolean("onduty",false);
               // mEditor.putString("job_id","0");
                mEditor.apply();
               checkAndSaveTheLocation(status);
            }
        });

//        handler.postDelayed(new Runnable() {
//            public void run() {
//                  saveTask();
//                handler.postDelayed(this, FIVE_SECONDS);
//            }
//        }, FIVE_SECONDS);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  anim_loading.setVisibility(View.VISIBLE);
               // myHomeListt.clear();
                if(LocalHelper.isConnectedToInternet(getActivity())) {

                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTheHomeDetailsFromServer();
                        }
                    },2000);

                }
                else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    anim_loading.setVisibility(View.GONE);
                    anim_nodatfound.setVisibility(View.VISIBLE);
                  //  Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

                }

                /* if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1"))
                {
                    myHomeListt.clear();
                    getTheHomeDetailsFromServer();

                }
                else {
                    myHomeListt.clear();
                }*/

            }
        });

        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // Toast.makeText(getActivity(), "interface calling ", Toast.LENGTH_SHORT).show();
            }
        };

        try {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //getLocation();


        return view;
    }



    private void checkforinprogresstask() {
       /* if(mytaskstatuslist.contains("Work In Progress"))
        {
          //  Toast.makeText(getActivity(), "already task in progress", Toast.LENGTH_SHORT).show();
            mEditor.putBoolean("workinprogress",true);
            mEditor.apply();

        }
        else {
            mEditor.putBoolean("workinprogress",false);
            mEditor.putString("job_id","0");
            mEditor.apply();

        }*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        proSwipeBtn.onDestroyView();
       
    }


    private void checkAndSaveTheLocation(String status) {
        switch (status){
            case "LOGIN" :
            case "RESUME" :
                // getLocation();
                 saveLocationRegularInterval();
                break;
            case "PAUSE" :
            case "LOGOUT" :
                //getLocation();

                removeCallbacks();
                break;

        }


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getLocation();
        }
    };


    public void saveLocationRegularInterval() {
        handler.postDelayed(runnable, 0);
    }

    public void removeCallbacks(){
        handler.removeCallbacks(runnable);
    }

    public void getLocation(){


     /*   Intent intent=new Intent(getActivity(),OtherActivity.class);
        startActivity(intent);
*/

        /*gpsTracker = new GpsTracker(getActivity());
        if(gpsTracker.canGetLocation()){
            AppDatabase db = AppDatabase.getDbInstance(getActivity());

            DbModelSendingData lastRecord =  db.taskDao().getLastRecordIfAvailable();

            String speed = "0";
            String idle = "0";
            if(lastRecord !=null){
                Location location = gpsTracker.getLocation();
                speed =  getspeed(location,lastRecord);
                idle = String.valueOf(checkTheDistance(location.getLatitude(),location.getLongitude(),lastRecord));
            }
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            DbModelSendingData task = new DbModelSendingData();
            task.setUserempId(sharedPreferences.getString(com.netiapps.planetwork.utils.Constants.userIdKey, ""));
            task.setLat(String.valueOf(latitude));
            task.setLng(String.valueOf(longitude));
            task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
            task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
            task.setStatus(sharedPreferences.getString("StartPlayOn",""));
            task.setCurrentTimeMillis(String.valueOf(System.currentTimeMillis()));
            task.setSpeed(speed);
            task.setIdle(idle);

            db.taskDao().insert(task);

            handler.postDelayed(runnable, FIVE_SECONDS);
        }else{
            removeCallbacks();
          //  gpsTracker.showSettingsAlert();
        }*/
    }


    int checkTheDistance(double latitude,double longitude, DbModelSendingData lastRecord){
        double venueLat = Double.parseDouble(lastRecord.getLat()); // Last known lat
        double venueLng = Double.parseDouble(lastRecord.getLng());// Last known lng

        double latDistance = Math.toRadians(latitude - venueLat);
        double lngDistance = Math.toRadians(longitude - venueLng);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(latitude))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = 6371 * c;
        if (dist<0.01){ //(in km, you can use 0.1 for metres etc.)
            /* If it's within 10m, we assume we're not moving */
            return 0;
        }
        return 1;
    }

    private String getspeed(Location location,DbModelSendingData lastRecord){
        double newTime= System.currentTimeMillis();
        double newLat = location.getLatitude();   // error
        double newLon = location.getLongitude();

        String speedString = "";

        double oldLat = Double.parseDouble(lastRecord.getLat());
        double oldLng = Double.parseDouble(lastRecord.getLng());
        double oldTime = Double.parseDouble(lastRecord.getCurrentTimeMillis());

        if(location.hasSpeed()){
            float speed = location.getSpeed();
            speedString = String.valueOf(speed);
            Toast.makeText(getActivity(),"SPEED : "+String.valueOf(speed)+"m/s",Toast.LENGTH_SHORT).show();
        } else {
            double distance = calculationBydistance(newLat,newLon,oldLat,oldLng);
            double timeDifferent = newTime - oldTime;
            double speed = distance/timeDifferent;
            speedString = String.valueOf(speed);
            Toast.makeText(getActivity(),"SPEED 2 : "+String.valueOf(speed)+"m/s",Toast.LENGTH_SHORT).show();
        }
        return speedString;
    }


    public double calculationBydistance(double lat1, double lon1, double lat2, double lon2){
        double radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_personProfile:
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.iv_ham_menu:

                break;
        }



    }




    private void saveTask() {

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                DbModelSendingData task = new DbModelSendingData();
                task.setUserempId(userID);
                task.setLat(String.valueOf(latitude));
                task.setLng(String.valueOf(longitude));
                task.setDate("");
                task.setTime("");
//                task.setPlay(Integer.parseInt(""));
//                task.setPause(Integer.parseInt(""));
         //       task.setLogout(false);

                //adding to database
//                DatabaseClient.getInstance(getActivity()).getAppDatabase()
//                        .taskDao()
//                        .insert(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity(new Intent(getActivity(), MainActivity.class));
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();

}

    public void scheduleSendLocation() {

        locationhandler.postDelayed(new Runnable() {
            public void run() {
                   // this method will contain your almost-finished HTTP calls

                String current_longitue=sharedPreferences.getString("longitide","0");
                String current_lattitude=sharedPreferences.getString("latitude","0");

             /*   Log.w("TAG","current_longitue"+current_longitue);
                Log.w("TAG","current_lattitude"+current_lattitude);*/

               // getandSendTheDetailsToServer(current_lattitude,current_longitue);

                Insert_data_to_local_databse(current_lattitude,current_longitue);


                locationhandler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);
    }

    private void Insert_data_to_local_databse(String current_lattitude, String current_longitue) {

        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        LatlangDatabse db = LatlangDatabse.getDbInstance(context);

        LatlangEntity latlangEntity= new LatlangEntity("6",current_lattitude,current_longitue,date,"1","1","10","1","1","1");
        db.getUserDao().insert(latlangEntity);
        Log.w("TAG","Updating db");

    }

//    public void getLocation(){
//        gpsTracker = new GpsTracker(getActivity());
//        if(gpsTracker.canGetLocation()){
//            latitude = gpsTracker.getLatitude();
//            longitude = gpsTracker.getLongitude();
//
//            Log.d("data", String.valueOf(latitude));
//
//        }else{
//            gpsTracker.showSettingsAlert();
//        }
//    }




    public interface OnFragmentInteractionListener {
        void openDrawerFromFragment();

        void closeDrawerFromFragment();
    }

    private void getandSendTheDetailsToServer(String current_lattitude, String current_longitue) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userID);
            jsonObject.put("lat",current_lattitude);
            jsonObject.put("long",current_longitue);
            jsonObject.put("Date_time","");
            jsonObject.put("projectid","");
            jsonObject.put("","");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
      //  progressDialog.show();

        Log.d(Constants.LOG, Constants.JOBS);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.JOBS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        parseTheJSONAndDisplayTheResults(response);
                    } else if (status == 0) {
                        String message = "No Jobs Found for the Day";

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


    private void getTheHomeDetailsFromServer() {

        if(LocalHelper.isConnectedToInternet(getActivity())) {
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
            progressDialog.show();*/

            Log.d(Constants.LOG, Constants.JOBS);
            JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.JOBS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  //  Log.d(Constants.LOG, response.toString());
                 //   progressDialog.dismiss();
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
        else {
            Toast.makeText(getActivity(),"No Internet Connection available ",Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {

        try {
            if(myHomeListt!=null) {
                myHomeListt.clear();
                mytaskstatuslist.clear();
            }

         /*   JSONObject jsonObject=new JSONObject(respodata);
            JSONArray jsonArray=jsonObject.getJSONArray("jobs");*/


           JSONArray jsonArray = response.getJSONArray("jobs");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                TasksDAO home = new TasksDAO();

             /*   String imagelist=categoryJSONObject.getString("images");
                Log.v("TAG","iiii "+imagelist);*/

                if(!categoryJSONObject.getString("status").equalsIgnoreCase(Constants.TASK_COMPLETED)) {
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
                    home.setTask(categoryJSONObject.getString("task_name"));
                    home.setTask_status(categoryJSONObject.getString("status"));
                    home.setSr_no(categoryJSONObject.getString("sr_no"));
                    // home.setImages(Collections.singletonList(categoryJSONObject.getString("images")));

                    myHomeListt.add(home);
                    mytaskstatuslist.add(categoryJSONObject.getString("status"));

                }

            }


        } catch (Exception e) {

        }

     //   Log.v("TAG","mytaskstatuslist "+mytaskstatuslist);
        if (myHomeListt.size() > 0) {


            adapterHomeTasks = new AdapterHomeTasks(getActivity(), myHomeListt);
            recyclerViewList.setHasFixedSize(true);
            recyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerViewList.setAdapter(adapterHomeTasks);
            recyclerViewList.setVisibility(View.VISIBLE);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.GONE);
          // checkforinprogresstask();
          /*  if(adapterHomeTasks==null) {

                adapterHomeTasks = new AdapterHomeTasks(getActivity(), myHomeListt);
                recyclerViewList.setHasFixedSize(true);
                recyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerViewList.setAdapter(adapterHomeTasks);
                recyclerViewList.setVisibility(View.VISIBLE);
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.GONE);
            }
            else {
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.GONE);
                adapterHomeTasks.notifyDataSetChanged();
            }*/

        } else {
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);
           // Toast.makeText(getActivity(), "NO Jobs Found For the Day", Toast.LENGTH_SHORT).show();
        }

    }

    private String getDateWithFormat(long timeInMillis,boolean getTheDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        String formattedDateOrTime;

        if(getTheDate) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            formattedDateOrTime = format.format(calendar.getTime());

        } else {

            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            formattedDateOrTime = df.format(calendar.getTime());
        }
        return formattedDateOrTime;

    }

    @Override
    public void onResume() {
        super.onResume();
      // Log.i("TAG","On Resume Called");
      //  getActivity().registerReceiver(broadcastReceiver, new IntentFilter("updatetext"));
        if(myHomeListt!=null)
        {
            myHomeListt.clear();
        }
        if(LocalHelper.isConnectedToInternet(getActivity())) {

            getTheHomeDetailsFromServer();

        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);

        }
    }

    public void updateText(String text){
        // Here you have it
        String data=text;
      //  Log.d("TAG","ot started "+data);

        proSwipeBtn.starttimer(getActivity());

    }

    public List<DbModelSendingData> getAllData(){

        List<DbModelSendingData> dbModelSendingDataList = new ArrayList<>();
        AppDatabase db = AppDatabase.getDbInstance(getActivity());
        dbModelSendingDataList = db.taskDao().getAllData();

        return dbModelSendingDataList;
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkConnectivityandLoadData(){
        myHomeListt.clear();
        if(LocalHelper.isConnectedToInternet(getActivity())) {

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getTheHomeDetailsFromServer();

                }
            },2000);


        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);
            return;
            //  Toast.makeText(getActivity(),"No Connection available",Toast.LENGTH_SHORT).show();

        }
    }

    private void call_attendednce_api(String type) {
        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        listDetails.put("type",type);
        listDetails.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(type.equalsIgnoreCase("logout")) {
            listDetails.put("login_id", sharedPreferences.getString("attendence_login_id", ""));
        }
        else {
            listDetails.put("login_id", "0");
        }

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().attendence(mHeaders, listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {

                    String result = new Gson().toJson(response.body());
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        if(type.equalsIgnoreCase("login")) {
                            String loginID = jsonObject.getString("login_id");
                            mEditor.putString("attendence_login_id", loginID);
                            mEditor.apply();
                        }
                        else {
                            mEditor.putString("attendence_login_id", "0");
                            mEditor.apply();
                        }
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

    /*private class TimerStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(LoginTimeCounterService.TIME_INFO)) {
                if (intent.hasExtra("VALUE")) {

                    int timeInSeconds= Integer.parseInt(intent.getStringExtra("VALUE"));
                    int hours = timeInSeconds / 3600;
                    int secondsLeft = timeInSeconds - hours * 3600;
                    int minutes = secondsLeft / 60;
                    int seconds = secondsLeft - minutes * 60;

                    String formattedTime = "";
                    if (hours < 10)
                        formattedTime += "0";
                    formattedTime += hours + ":";

                    if (minutes < 10)
                        formattedTime += "0";
                    formattedTime += minutes + ":";

                    if (seconds < 10)
                        formattedTime += "0";
                    formattedTime += seconds ;

                   if(timeInSeconds>=logouthour)
                   {
                       mEditor.putString("session_loggedin","0");
                       mEditor.apply();
                   }


                }
            }
        }
    }*/
}

