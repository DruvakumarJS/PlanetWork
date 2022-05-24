package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.ProfileActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.locationbackground.GetLocation_Service;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.model.TasksDAO;
import com.netiapps.planetwork.mvvm.AdapterHomeTasksmvvm;
import com.netiapps.planetwork.mvvm.Jobs;
import com.netiapps.planetwork.mvvm.TaskRepository;
import com.netiapps.planetwork.mvvm.UserTaskDAO;
import com.netiapps.planetwork.roomdatabase.LatlangDatabse;
import com.netiapps.planetwork.roomdatabase.LatlangEntity;
import com.netiapps.planetwork.roomdatabase.latlangDAO;
import com.netiapps.planetwork.services.GpsTracker;
import com.netiapps.planetwork.swipe.ProSwipeButton;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragmentmvvm extends Fragment implements View.OnClickListener{

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
  //  private MyConsignmnetAdapter myConsignmnetAdapter;
    private AdapterHomeTasksmvvm adapterHomeTasks;

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

    //MVVM
    private TaskRepository taskviewmodelreposotory;
      ArrayList<UserTaskDAO> tasklist=new ArrayList<>();
      ArrayList<Jobs>jobs=new ArrayList<>();
    AdapterHomeTasksmvvm adapterHomeTasksmvvm;

    public HomeFragmentmvvm() {

    }

        String respodata="{\n" +
                "  \"status\": 1,\n" +
                "  \"jobs\": [\n" +
                "    {\n" +
                "      \"sr_no\": \"SR005\",\n" +
                "      \"job_id\": 5,\n" +
                "      \"job_assign_id\": 5,\n" +
                "      \"job_date\": \"2022-01-25\",\n" +
                "      \"customer_name\": \"KK and Co\",\n" +
                "      \"branch\": \"T NAgar\",\n" +
                "      \"address\": \"120\\/76, Gopathi Narayanaswami Chetty Rd, Parthasarathi Puram, T. Nagar, Chennai, Tamil Nadu 600017, India\",\n" +
                "      \"latitude\": \"13.0424011\",\n" +
                "      \"longitude\": \"80.234968\",\n" +
                "      \"city\": \"Chennai\",\n" +
                "      \"status\": \"pending\",\n" +
                "      \"no_of_visit\": \"0\",\n" +
                "      \"images\":[{\"image\":\"https://cdn.vox-cdn.com/thumbor/pK9C73TluHQNnsynCrgIjGU0Kio=/0x0:2040x1360/920x613/filters:focal(857x517:1183x843):format(webp)/cdn.vox-cdn.com/uploads/chorus_image/image/67564720/dbohn_200608_4059_0008.0.jpg\"},\n" +
                "                {\"image\":\"https://st.depositphotos.com/2245963/2392/i/600/depositphotos_23929049-stock-photo-android-robot.jpg\"},\n" +
                "                {\"image\":\"https://9to5google.com/wp-content/uploads/sites/4/2021/10/One-UI-4.0-Android-12-4.jpg?quality=82&strip=all&w=1600\"}]\n" +
                "    },\n" +
                "    {\n" +
                "      \"sr_no\": \"SR006\",\n" +
                "      \"job_id\": 6,\n" +
                "      \"job_assign_id\": 7,\n" +
                "      \"job_date\": \"2022-01-27\",\n" +
                "      \"customer_name\": \"KK and Co\",\n" +
                "      \"branch\": \"MG Road Bangalore\",\n" +
                "      \"address\": \"Mahatma Gandhi Rd, Yellappa Chetty Layout, Sivanchetti Gardens, Bengaluru, Karnataka, India\",\n" +
                "      \"latitude\": \"12.9731734\",\n" +
                "      \"longitude\": \"77.6166073\",\n" +
                "      \"city\": \"Bangalore\",\n" +
                "      \"status\": \"Work In Progress\",\n" +
                "      \"no_of_visit\": \"1\",\n" +
                "      \"images\":[]\n" +
                "    },\n" +
                "    {\n" +
                "      \"sr_no\": \"SR007\",\n" +
                "      \"job_id\": 7,\n" +
                "      \"job_assign_id\": 8,\n" +
                "      \"job_date\": \"2022-01-24\",\n" +
                "      \"customer_name\": \"Netiapps\",\n" +
                "      \"branch\": \"Bangalore\",\n" +
                "      \"address\": \"BLOCK-1, PRESTIGE NOTTING HILL, Kalena Agrahara, Bengaluru, कर्नाटक 560076, India\",\n" +
                "      \"latitude\": \"12.872647\",\n" +
                "      \"longitude\": \"77.5950289\",\n" +
                "      \"city\": \"Bangalore\",\n" +
                "      \"status\": \"Work On Hold\",\n" +
                "      \"no_of_visit\": \"1\",\n" +
                "       \"images\":[{\"image\":\"http://placehold.it/120x120&text=image1\"},\n" +
                "                {\"image\":\"http://placehold.it/120x120&text=image2\"},\n" +
                "                {\"image\":\"http://placehold.it/120x120&text=image3\"},]\n" +
                "    },\n" +
                "    {\n" +
                "      \"sr_no\": \"SR008\",\n" +
                "      \"job_id\": 8,\n" +
                "      \"job_assign_id\": 9,\n" +
                "      \"job_date\": \"2022-01-22\",\n" +
                "      \"customer_name\": \"Test Data Data\",\n" +
                "      \"branch\": \"Bangalore\",\n" +
                "      \"address\": \"HRBR Layout, Kalyan Nagar, Bengaluru, Karnataka 560043, India\",\n" +
                "      \"latitude\": \"13.0191445\",\n" +
                "      \"longitude\": \"77.6464534\",\n" +
                "      \"city\": \"Bangalore\",\n" +
                "      \"status\": \"Completed\",\n" +
                "      \"no_of_visit\": \"1\",\n" +
                "      \"images\":[\n" +
                "  {\n" +
                "    \"image\": \"https://cdn.vox-cdn.com/thumbor/pK9C73TluHQNnsynCrgIjGU0Kio=/0x0:2040x1360/920x613/filters:focal(857x517:1183x843):format(webp)/cdn.vox-cdn.com/uploads/chorus_image/image/67564720/dbohn_200608_4059_0008.0.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"image\": \"https://st.depositphotos.com/2245963/2392/i/600/depositphotos_23929049-stock-photo-android-robot.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"image\": \"https://9to5google.com/wp-content/uploads/sites/4/2021/10/One-UI-4.0-Android-12-4.jpg?quality=82&strip=all&w=1600,\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"image\": \"https://st.depositphotos.com/2245963/2392/i/600/depositphotos_23929049-stock-photo-android-robot.jpg\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"image\": \"https://cdn.vox-cdn.com/thumbor/pK9C73TluHQNnsynCrgIjGU0Kio=/0x0:2040x1360/920x613/filters:focal(857x517:1183x843):format(webp)/cdn.vox-cdn.com/uploads/chorus_image/image/67564720/dbohn_200608_4059_0008.0.jpg\"\n" +
                "  }\n" +
                "]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

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

        taskviewmodelreposotory = new ViewModelProvider(requireActivity()).get(TaskRepository.class);

        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        mEditor.putBoolean(Constants.REVERSE_FIRST_TIME, true);
        mEditor.apply();

        userName = sharedPreferences.getString(Constants.userNameKey, "");
        userID = sharedPreferences.getString(Constants.userIdKey, "");
        profilephoto = sharedPreferences.getString(Constants.photo, "");

        tvName = view.findViewById(R.id.title);

        tvName.setText("Hi , " + userName);

        ivHamMenu = view.findViewById(R.id.iv_ham_menu);
        mSwipeRefreshLayout=view.findViewById(R.id.swipecontainer);
        imgPriofile = (ImageView) view.findViewById(R.id.iv_personProfile);

        imgPriofile.setBackgroundResource(R.drawable.ic_person);
        anim_nodatfound=view.findViewById(R.id.ivnodatafound);
        anim_loading=view.findViewById(R.id.loading);

        recyclerViewList = view.findViewById(R.id.recyclerView);
        myHomeListt = new ArrayList<>();
        listdateText = new ArrayList<TextView>();
        listcompanyText = new ArrayList<TextView>();
        listbranchText = new ArrayList<TextView>();
        liststatus = new ArrayList<TextView>();

        ivHamMenu.setOnClickListener(this);
        imgPriofile.setOnClickListener(this);

     //scheduleSendLocation();
        if(LocalHelper.isConnectedToInternet(getActivity())) {
            anim_loading.setVisibility(View.VISIBLE);
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   // getTheHomeDetailsFromServer();

                    recyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    getTasksusingmvvm();
                    recyclerViewList.setItemAnimator(new DefaultItemAnimator());

                }
            },2000);

        }
        else {
            mSwipeRefreshLayout.setRefreshing(false);
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);

        }

        proSwipeBtn = view.findViewById(R.id.proswipebutton_main);

            /*  GetLocation_Service locationService = new GetLocation_Service();
        mServiceIntent = new Intent(getActivity(), locationService.getClass());

        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                GetLocation_Service.LocalBinder binder = (GetLocation_Service.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                mService.setCallbacks(getActivity());
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }

        };

        if (context != null && !Functions.isMyServiceRunning(context, locationService.getClass())) {
            context.startService(mServiceIntent);
            context.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }*/

            proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        proSwipeBtn.showResultIcon(true, false);
                        if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1"))
                        {

                       /*  if (context != null && !Functions.isMyServiceRunning(context, locationService.getClass())) {
                                context.startService(mServiceIntent);
                                context.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
                            }
*/


                        }

                        /*else {
                           mSwipeRefreshLayout.setRefreshing(false);
                            Log.v("TAG","session_loggedin "+"false");
                            //Toast.makeText(getActivity(),"logout ",Toast.LENGTH_SHORT).show();

                            context.unbindService(mConnection);
                            context.stopService(mServiceIntent);

                            myHomeListt.clear();
                            recyclerViewList.setAdapter(null);


                        }*/
                    }
                }, 2000);
            }
        });

        proSwipeBtn.startStop(new ProSwipeButton.LoginStartStopListener() {
            @Override
            public void startStop(String status) {
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
        taskviewmodelreposotory = new ViewModelProvider(requireActivity()).get(TaskRepository.class);

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
                Log.d(Constants.LOG, response.toString());
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
                    Log.d(Constants.LOG, response.toString());
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
          //  myHomeListt.clear();

            JSONObject jsonObject=new JSONObject(respodata);
            JSONArray jsonArray=jsonObject.getJSONArray("jobs");


           // JSONArray jsonArray = response.getJSONArray("jobs");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                TasksDAO home = new TasksDAO();

             /*   String imagelist=categoryJSONObject.getString("images");
                Log.v("TAG","iiii "+imagelist);*/
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
                home.setImages(Collections.singletonList(categoryJSONObject.getString("images")));

                myHomeListt.add(home);

            }


        } catch (Exception e) {

        }

        if (myHomeListt.size() > 0) {

            if(adapterHomeTasks==null) {

                adapterHomeTasks = new AdapterHomeTasksmvvm(getActivity(), myHomeListt);
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
            }

        } else {
            anim_loading.setVisibility(View.GONE);
            anim_nodatfound.setVisibility(View.VISIBLE);
           // Toast.makeText(getActivity(), "NO Jobs Found For the Day", Toast.LENGTH_SHORT).show();
        }

    }

 /*   public class MyConsignmnetAdapter extends RecyclerView.Adapter<MyConsignmnetAdapter.ViewHolder> {

        private List<Home> inputList = new ArrayList<>();
        private List<Home> itemList = new ArrayList<>();
        private Context context;
        int index=0;

        public MyConsignmnetAdapter(Context context, List<Home> items) {
            if (items != null && !items.isEmpty()) {
                itemList = items;
                inputList = items;
            }
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Home homeData = itemList.get(position);

            String no_of_visits=homeData.getNo_of_visit().trim();

            holder.tvdate.setText(homeData.getJob_date());
           // holder.tvcompnayJob.setText(homeData.getCustomer_name());
            holder.tvbrnchSub.setText(homeData.getBranch());
            holder.tvcount.setText(homeData.getNo_of_visit());
            holder.tvTaskName.setText(homeData.getTask());
            holder.tvSRnumber.setText("SR NO : "+homeData.getSr_no());

               String jobStatus=homeData.getJob_status();

            if ("pending".equalsIgnoreCase(homeData.getJob_status())){
                holder.tvPending.setText(Constants.TASK_PENDING);
                holder.tvPending.setBackgroundResource(R.drawable.border_button);
                holder.tvPending.setTextColor(R.color.pedingyelow);
            } else  if("Work In Progress".equalsIgnoreCase(homeData.getJob_status())){
                holder.tvPending.setText(Constants.TASK_IN_PROGRESS);
                holder.tvPending.setBackgroundResource(R.drawable.complerejected);
                holder.tvPending.setTextColor(R.color.rejectred);
            } else  if("Completed".equalsIgnoreCase(homeData.getJob_status())) {
                holder.tvPending.setText(Constants.TASK_COMPLETED);
                holder.tvPending.setBackgroundResource(R.drawable.completed_back);
                holder.tvPending.setTextColor(R.color.approvegrrenn);
            }
              else if("Work On Hold".equalsIgnoreCase(homeData.getJob_status())) {
                holder.tvPending.setText(Constants.TASK_IN_HOLD);
                holder.tvPending.setBackgroundResource(R.drawable.onhold_back);
                holder.tvPending.setTextColor(R.color.onholdyellow);
            }

            listdateText.add(holder.tvdate);
            listbranchText.add(holder.tvbrnchSub);
            listcompanyText.add(holder.tvcompnayJob);
            liststatus.add(holder.tvPending);


            holder.cardlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = holder.getAdapterPosition();
                    notifyDataSetChanged();
                }

            });

            if (index == position)
            {
                holder.cardlayout.setCardBackgroundColor(context.getResources().getColor(R.color.selector));

            }
            else {
                holder.cardlayout.setCardBackgroundColor(context.getResources().getColor(R.color.white));

            }

            holder.holdercardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent itent = new Intent(getActivity(), TaskDetails.class);
                    itent.putExtra("Date",homeData.getJob_date());
                    itent.putExtra("Copany",homeData.getCustomer_name());
                    itent.putExtra("task",homeData.getTask());
                    itent.putExtra("Branch",homeData.getBranch());
                    itent.putExtra("JobAssignId",homeData.getJob_assign_id());
                    itent.putExtra("NoOfVisit",homeData.getNo_of_visit());
                    itent.putExtra("JobId",homeData.getJob_id());
                    itent.putExtra("task_status",homeData.getTask_status());
                    itent.putExtra("sr_no",homeData.getSr_no());
                    startActivity(itent);
                }
            });

            holder.rllocationlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat=homeData.getLatitude();
                    String lang=homeData.getLongitude();
                   // Uri gmmIntentUri = Uri.parse("google.navigation:q=12.9767,77.5713");
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lang);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                   // Toast.makeText(context, "open Maps ", Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void filterItems(String filter) {
            if (TextUtils.isEmpty(filter)) {
                itemList = inputList;
            } else {
                List<Home> filteredList = new ArrayList<>();
                for (Home item : inputList) {
                    if (item.job_status.toUpperCase().contains(filter.toUpperCase()))
                        filteredList.add(item);
                }
                itemList = filteredList;
            }
            notifyDataSetChanged();
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

            TextView tvdate;
            TextView tvbrnchSub;
            TextView tvcompnayJob;
            TextView tvPending;
            TextView tvcount;
            TextView tvTaskName;
            TextView tvSRnumber;
            RelativeLayout holdercardview;
            View itemView;
            CardView cardlayout;
            RelativeLayout rllocationlayout;


            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvdate = (TextView) itemView.findViewById(R.id.tvdate);
               // tvcompnayJob = (TextView) itemView.findViewById(R.id.tvcompnayJob);
                tvbrnchSub = (TextView) itemView.findViewById(R.id.tvbrnchSub);
                tvPending= (TextView) itemView.findViewById(R.id.tvPending);
                tvcount = (TextView) itemView.findViewById(R.id.tvcount);
                holdercardview = itemView.findViewById(R.id.rldatalayout);
                tvTaskName=itemView.findViewById(R.id.tvTaskanme);
                rllocationlayout=itemView.findViewById(R.id.lllocation);
                cardlayout=itemView.findViewById(R.id.card_holder);
                tvSRnumber=itemView.findViewById(R.id.tvsrnumber);


            }
        }
    }
*/

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DATA_SAVED_SUCCESSFULLY){
            if(resultCode == RESULT_OK) {
                if(data != null){
                    int positionMain = 0;
                    String  consgNumber = data.getStringExtra(Keys.JOB_STATUS);
                    for(int i = 0 ; i < myHomeListt.size() ; i++){
                        Home myConsignment  = myHomeListt.get(i);
                        if(myConsignment.getJob_status().equalsIgnoreCase(consgNumber)){
                            positionMain = i;
                        }
                    }
                    myHomeListt.remove(positionMain);
                    myConsignmnetAdapter.filterItems(tvStatus.getText().toString().trim());
                    //tvCount.setText(myConsignmentListt.size() + " ");

                }
            }
        }

    }*/


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

      //  getActivity().registerReceiver(broadcastReceiver, new IntentFilter("updatetext"));
       // getTheHomeDetailsFromServer();

    }

    public void updateText(String text){
        // Here you have it
        String data=text;
        Log.v("TAG","ot started "+data);

        proSwipeBtn.starttimer(getActivity());

    }
    private void getTasksusingmvvm() {
        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));


        HashMap<String, Object> listDetails = new HashMap<>();
        listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));


        taskviewmodelreposotory.getalltasks(mHeaders,listDetails).observe(requireActivity(),userTaskDAOS -> {

            tasklist.add(userTaskDAOS);

            String response= new Gson().toJson(userTaskDAOS);
            //Log.v("TAG",""+response);
            try {
                JSONObject jsonObject=new JSONObject(response);

                String status=jsonObject.getString("status");

                if(status.matches("1")){
                    JSONArray jsonArray=jsonObject.getJSONArray("jobs");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                        TasksDAO home = new TasksDAO();

             /*   String imagelist=categoryJSONObject.getString("images");
                Log.v("TAG","iiii "+imagelist);*/
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
                       // home.setImages(Collections.singletonList(categoryJSONObject.getString("images")));

                        myHomeListt.add(home);

                    }

                }

                adapterHomeTasks = new AdapterHomeTasksmvvm(getActivity(), myHomeListt);
                recyclerViewList.setHasFixedSize(true);
                recyclerViewList.setAdapter(adapterHomeTasks);
                recyclerViewList.setVisibility(View.VISIBLE);
                anim_loading.setVisibility(View.GONE);
                anim_nodatfound.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }



        });


    }


}

