package com.netiapps.planetwork;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.netiapps.planetwork.broadcast.AlarmReceiver;
import com.netiapps.planetwork.broadcast.SendLocationToServerBroadcastReceiver;
import com.netiapps.planetwork.fragments.HomeFragment;
import com.netiapps.planetwork.fragments.HomeFragmentmvvm;
import com.netiapps.planetwork.fragments.NotificationFragment;
import com.netiapps.planetwork.fragments.OverTimeFragment;
import com.netiapps.planetwork.fragments.PieChartActivity;
import com.netiapps.planetwork.fragments.TaskFragment;
import com.netiapps.planetwork.locationbackground.Callback;
import com.netiapps.planetwork.locationbackground.Functions;
import com.netiapps.planetwork.locationbackground.GetLocation_Service;
import com.netiapps.planetwork.roomdatabase.latlangDAO;
import com.netiapps.planetwork.services.GPSstatusService;
import com.netiapps.planetwork.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class DashBoardActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, OverTimeFragment.otStarted, NavigationView.OnNavigationItemSelectedListener{

    private static final Object MY_PERMISSIONS_REQUEST_LOCATION = 200;
    private ImageView imgHome;
    private ImageView imgNotification;
    private ImageView imgTask;
    private ImageView imgovertime;
    private ImageView img_report;
    private Toolbar toolbar;
    private ImageView imgPriofile;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private TextView tvEmplid,tvPhone;
    String NotificationData;
    final Fragment homeFragment = new HomeFragment();
    HomeFragment f;
    final FragmentManager fm = getSupportFragmentManager();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;
    Intent gpsintent;
    int  ELAPSED_TIME_TO_SAVE_IN_SERVER = 5 * 60 * 1000;
    private static final int PERMISSION_REQUEST_CODE = 202;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HomeFragment home_frament= new HomeFragment();
    NotificationFragment notification_fragment = new NotificationFragment();
    TaskFragment task_fragment =new TaskFragment();
    OverTimeFragment ot_fragment=new OverTimeFragment();
    PieChartActivity piechart_fragment=new PieChartActivity();

    //BroadcastReceiver receiver;

    int newsecound=0;

    private MeowBottomNavigation bnv_Main;
    private Context context;

    GpsStatusReceiver receiver = new GpsStatusReceiver();
    GetLocation_Service mService;
   // OfflineLocationService mService;
    boolean mBound = false;
    private final String PACKAGE_URL_SCHEME = "package:";

    private latlangDAO latlangDAO;
    //ServiceConnection mConnection;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GetLocation_Service.LocalBinder binder = (GetLocation_Service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCallbacks(DashBoardActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };

    /*ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            OfflineLocationService.LocalBinder binder = (OfflineLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCallbacks(DashBoardActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);

        context = getApplicationContext();

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        imgHome = findViewById(R.id.img_home);
        imgNotification = findViewById(R.id.img_notification);
        imgTask = findViewById(R.id.imgtask);
        imgovertime = findViewById(R.id.img_task);
        img_report = findViewById(R.id.img_report);

        navigationView=findViewById(R.id.nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        imgPriofile = findViewById(R.id.iv_personProfile);
        imgPriofile.setBackgroundResource(R.drawable.ic_person);

    //    Log.d("TAG","ot_started"+mSharedPreferences.getString("geofencetaskid","0"));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(headerView);

        tvEmplid=headerView.findViewById(R.id.tvemplid);
        tvPhone=headerView.findViewById(R.id.tvphone);

        tvEmplid.setText("Emp ID : PW2022A"+mSharedPreferences.getString(Constants.userIdKey,""));
        tvPhone.setText("Mobile : "+mSharedPreferences.getString(Constants.mobileNumber,""));

        startLocationSavingToServer();

        String datetime= new SimpleDateFormat("HH:mm:ss").format(new Date());
        String time= "00:00:00";
      /*  mEditor.putString("job_id","1");
        mEditor.apply();*/
       /* mEditor.putBoolean("isresetTaskSelection",false);
        mEditor.apply();*/

        if(datetime.compareTo(time)>0 && !mSharedPreferences.getBoolean("isresetTaskSelection",false) && !mSharedPreferences.getBoolean("insideGeofence",false))
        {
            mEditor.putString("job_id","0");
            mEditor.putBoolean("isresetTaskSelection",true);
            mEditor.putBoolean("workinprogress",false);
            mEditor.putBoolean("insideGeofence",false);
            mEditor.apply();
        }

       // startService(new Intent(DashBoardActivity.this, ConnectivityService.class));

        startService(new Intent(DashBoardActivity.this, GPSstatusService.class));

       registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean result = checkPermissions();
            if (result) {
                bindLocationService();
            }
        }
        checkLocation();

       // FireNotification("Testing","Planetwork Solid Test");

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                      //  String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(DashBoardActivity.this, "Firebase token"+token, Toast.LENGTH_SHORT).show();
                    }
                });




        bnv_Main = findViewById(R.id.bnv_Main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /*bnv_Main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
            bnv_Main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_group_679__1_));
            bnv_Main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_group_680));
            bnv_Main.add(new MeowBottomNavigation.Model(4, R.drawable.ic_activity));
            bnv_Main.add(new MeowBottomNavigation.Model(5, R.drawable.ic__3252117381571183079_1));*/
            bnv_Main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home_1));
            bnv_Main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_task_1));
            bnv_Main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_add_1));
            bnv_Main.add(new MeowBottomNavigation.Model(4, R.drawable.ic_piechart_1));
            bnv_Main.add(new MeowBottomNavigation.Model(5, R.drawable.ic_notification_1));
        }
        else {
            bnv_Main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home_1));
            bnv_Main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_task_1));
            bnv_Main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_add_1));
            bnv_Main.add(new MeowBottomNavigation.Model(4, R.drawable.ic_piechart_1));
            bnv_Main.add(new MeowBottomNavigation.Model(5, R.drawable.ic_notification_1));
        }

        bnv_Main.show(1,true);
       // add(new HomeFragment());

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, home_frament,"home");
        fragmentTransaction.commit();

        bnv_Main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        //toolbar.setVisibility(View.VISIBLE);
                        if(getSupportFragmentManager().findFragmentByTag("home")==null){

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(notification_fragment)
                                    .hide(task_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .hide(piechart_fragment)
                                    .commit();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content, home_frament,"home");
                            fragmentTransaction.commit();

                        }
                        else {

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(home_frament)
                                    .hide(notification_fragment)
                                    .hide(ot_fragment)
                                    .hide(task_fragment)
                                    .hide(piechart_fragment)
                                    .commit();
                        }

                        //loadFragment(new HomeFragment());
                        break;

                    case 5:
                        //loadFragment(new NotificationFragment());
                        toolbar.setVisibility(View.GONE);
                        if(getSupportFragmentManager().findFragmentByTag("notification")==null){

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(task_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .hide(piechart_fragment)
                                    .commit();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content, notification_fragment,"notification");
                            fragmentTransaction.show(notification_fragment);
                            fragmentTransaction.commit();

                        }
                        else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(notification_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .hide(task_fragment)
                                    .hide(piechart_fragment)
                                    .commit();
                        }
                        break;

                    case 2:
                        //loadFragment(new TaskFragment());
                        toolbar.setVisibility(View.GONE);
                        if(getSupportFragmentManager().findFragmentByTag("task")==null){
/*

                    if(getSupportFragmentManager().findFragmentByTag("home")!=null){getSupportFragmentManager().beginTransaction().hide(home_frament);}
                    if(getSupportFragmentManager().findFragmentByTag("notification")!=null){getSupportFragmentManager().beginTransaction().hide(notification_fragment);}
                    if(getSupportFragmentManager().findFragmentByTag("ot")!=null){getSupportFragmentManager().beginTransaction().hide(ot_fragment);}
                    if(getSupportFragmentManager().findFragmentByTag("piechart")!=null){getSupportFragmentManager().beginTransaction().hide(piechart_fragment);}
*/
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(notification_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .hide(piechart_fragment)
                                    .commit();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content, task_fragment,"task");
                            fragmentTransaction.show(task_fragment);
                            fragmentTransaction.commit();

                        }
                        else {

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(task_fragment)
                                    .hide(notification_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .hide(piechart_fragment)
                                    .commit();
                        }



                        break;

                    case 3:
                        toolbar.setVisibility(View.GONE);
                        // loadFragment(new OverTimeFragment());
                        if(getSupportFragmentManager().findFragmentByTag("ot")==null){

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(notification_fragment)
                                    .hide(task_fragment)
                                    .hide(home_frament)
                                    .hide(piechart_fragment)
                                    .commit();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content, ot_fragment,"ot");
                            fragmentTransaction.show(ot_fragment);
                            fragmentTransaction.commit();

                        }
                        else {

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(ot_fragment)
                                    .hide(notification_fragment)
                                    .hide(home_frament)
                                    .hide(task_fragment)
                                    .hide(piechart_fragment)
                                    .commit();
                        }

                        break;

                    case 4:
                        // loadFragment(new PieChartActivity());
                        toolbar.setVisibility(View.GONE);
                        if(getSupportFragmentManager().findFragmentByTag("piechart")==null){

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .hide(notification_fragment)
                                    .hide(task_fragment)
                                    .hide(home_frament)
                                    .hide(ot_fragment)
                                    .commit();

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content, piechart_fragment,"piechart");
                            fragmentTransaction.show(piechart_fragment);
                            fragmentTransaction.commit();

                        }
                        else {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(piechart_fragment)
                                    .hide(ot_fragment)
                                    .hide(task_fragment)
                                    .hide(notification_fragment)
                                    .hide(home_frament)
                                    .commit();
                        }
                }
                return null;
            }
        });

        Intent notificationdata = getIntent();
        if (notificationdata != null && notificationdata.getExtras() != null) {
            Bundle extras = notificationdata.getExtras();
            Log.d("TAG","notification data "+extras);
            Log.d("TAG","notification data "+extras.getString("lat"));
        }


    }

    /*private void add(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content,fragment);
        transaction.commit();
    }*/


    private void FireNotification(String messsage, String tittle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.nt);  //Here is FILE_NAME is the name of file that you want to play

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(tittle)
                        .setContentText(messsage)
                        // .setLargeIcon(bmp)
                        .setContentIntent(pendingIntent)
                        /* .setStyle(
                                 new NotificationCompat.BigPictureStyle().bigPicture(bmp))*/
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        else {
            notificationManager=
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(sound != null){
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(channelId,"pp",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(sound,audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Random r = new Random();
            int randomNumber = r.nextInt(10);
            notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());

        }
        else {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        }
    }

    private void Start_Broadcast_for_GPS() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        sendBroadcast(intent);
    }

    private void checkforgpsenabled() {

        //alarmManager.cancel(pendingIntent);

        gpsintent = new Intent(this, AlarmReceiver.class);
        gpsintent.setAction("alarm.running");
        pendingIntent = PendingIntent.getBroadcast(this, 0, gpsintent, 0);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        long interval = 1 * 60 * 1000;

        Calendar calendar = Calendar.getInstance();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+interval,interval, pendingIntent);


    }


    public void startLocationSavingToServer() {

        intent = new Intent(this, SendLocationToServerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, ELAPSED_TIME_TO_SAVE_IN_SERVER, ELAPSED_TIME_TO_SAVE_IN_SERVER
                , pendingIntent);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void openDrawerFromFragment() {
        // Toast.makeText(DashBoardActivity.this,"lll",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void closeDrawerFromFragment() {
// Toast.makeText(DashBoardActivity.this,"lll",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   Log.v("TAG","lifecycle - "+"backpressed");
        mEditor.putString("back_button_invoked","1");
        mEditor.apply();



    }

    @Override
    protected void onStop() {
        super.onStop();

       // Log.v("TAG","lifecycle - "+"onStop");
        String onstoptimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

       // int total_seconds = mSharedPreferences.getInt("total_seconds", 0);

       // mEditor.putInt("total_seconds",total_seconds);

        mEditor.putString("timer_stopped_time", onstoptimeStamp);
        mEditor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEditor.putString("back_button_invoked","0");
      //  Log.v("TAG","lifecycle - "+"onResume");

        String current_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String login_time = mSharedPreferences.getString("login_time", current_time);
        String time_at_destroy=mSharedPreferences.getString("time_at_destroy",current_time);

        String brodcast_logout_time = mSharedPreferences.getString("broadcast_logout_time", current_time);

        // broadcast data
        String timer_stopped_time = mSharedPreferences.getString("broadcast_time", "00:00");
        String forcestoptimer = mSharedPreferences.getString("forcestoptimer", "0");


        int  total_second = mSharedPreferences.getInt("total_seconds", 0);
        int input_secounds=0;


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        Date date2 = null;
        Date date3=null;
        Date force_logout_timer = null;

        try {
            date1 = format.parse(current_time);
            date2 = format.parse(login_time);
            date3=format.parse(time_at_destroy);

            force_logout_timer=format.parse(brodcast_logout_time);

       /*     Log.v("TAG","current time"+current_time);
            Log.v("TAG","login_time"+login_time);
            Log.v("TAG","time_at_destroy"+time_at_destroy);
            Log.v("TAG","total_second"+total_second);*/


            long difference = (date1.getTime() - date2.getTime());// in millis sec
            long timeinetrval= (date1.getTime() - date3.getTime());

           /* Log.v("TAG","difference"+difference);
            Log.v("TAG","timeinetrval"+timeinetrval);
*/
            String timer_paused=mSharedPreferences.getString("timer_paused","false");

            if(forcestoptimer.equalsIgnoreCase("1") || timer_paused.equalsIgnoreCase("true"))
            {

                input_secounds=total_second;


            }

            else if(mSharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("0"))
                {
                    input_secounds=0;

                }
            else if(mSharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1") && timer_paused.equalsIgnoreCase("true"))
            {
                input_secounds=total_second;

            }

            else if(timer_paused.equalsIgnoreCase("false") && mSharedPreferences.getString("back_button_invoked","0").equalsIgnoreCase("0")
                    && mSharedPreferences.getString("isdestroyed","ll").equalsIgnoreCase("1"))
            {
                input_secounds = total_second+ (int) (timeinetrval / 1000);

            }


            else if(mSharedPreferences.getString("back_button_invoked","0").equalsIgnoreCase("0")
            && timer_paused.equalsIgnoreCase("false"))
            {
                input_secounds=total_second;

            }

            else if(timer_paused.equalsIgnoreCase("false") && mSharedPreferences.getString("back_button_invoked","0").equalsIgnoreCase("1"))
                {
                    input_secounds = total_second+ (int) (timeinetrval / 1000);


            }

            else {
                input_secounds=total_second;

            }

            mEditor.putInt("total_seconds", input_secounds);
            mEditor.apply();


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("TAG","lifecycle - "+"onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("TAG","lifecycle - "+"onRestart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TAG","lifecycle - "+"onDestroy");
        String destroy_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

       // mEditor.putString("time_at_destroy",destroy_time);
        mEditor.putString("isdestroyed","1");
        mEditor.apply();
       // unregisterReceiver(gpSstatusBroadcast);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("TAG","lifecycle - "+"onStart");

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(DashBoardActivity.this);
        builder1.setMessage("Your Internet/ GPS Connection is lost . Please connect to internet and GPS and then continue.. ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
                        finish();
                    }
                });
/*
        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });*/

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(DashBoardActivity.this, PERMISSIONS)) {
            Functions.customAlertDialog(DashBoardActivity.this, new Callback() {
                @Override
                public void Responce(String resp) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }
            });
        } else {
            return true;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            bindLocationService();
        }

        return false;
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }


    private void checkLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        boolean isEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isEnabled) {
            enableLocation();
        }
        else {
          //  bindLocationService();
            Intent intent=new Intent(DashBoardActivity.this, GetLocation_Service.class);
            //startService(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
            else {
                startService(intent);
            }
        }
    }

    private void enableLocation() {
      /*  startActivity(new Intent(DashBoardActivity.this, Enablelocation_A.class));
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);*/


    }

   /* private void bindLocationService() {
        GetLocation_Service locationService = new GetLocation_Service();
        Intent mServiceIntent = new Intent(context, locationService.getClass());
        if (context != null && !Functions.isMyServiceRunning(context, locationService.getClass())) {
            context.startService(mServiceIntent);
            context.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }*/
   private void bindLocationService() {
       GetLocation_Service locationService = new GetLocation_Service();
       Intent mServiceIntent = new Intent(context, locationService.getClass());
       if (context != null && !Functions.isMyServiceRunning(context, locationService.getClass())) {
           context.startService(mServiceIntent);
           context.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
       }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindLocationService();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    } else {
                        Functions.customAlertDialogDenied(DashBoardActivity.this, new Callback() {
                            @Override
                            public void Responce(String resp) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                                startActivity(intent);
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public void starttimer(String text) {
        HomeFragment home = (HomeFragment)
                getSupportFragmentManager().findFragmentByTag("home");
        home.updateText(text);

        mEditor.putInt("total_seconds",0);
        mEditor.apply();

        bnv_Main.show(1,true);
        getSupportFragmentManager()
                .beginTransaction()
                .hide(notification_fragment)
                .hide(task_fragment)
                .show(home_frament)
                .hide(ot_fragment)
                .hide(piechart_fragment)
                .commit();

       /* FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, notification_fragment,"notification");
        fragmentTransaction.commit();*/

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the camera action
            startActivity(new Intent(DashBoardActivity.this , ProfileActivity.class));
            finish();
        }
        else  if(id==R.id.attendence)
        {
            startActivity(new Intent(DashBoardActivity.this , AttendenceActivity.class));
        }
        else if(id==R.id.reports){
            startActivity(new Intent(DashBoardActivity.this , ReportListActivity.class));
        }
        else if(id==R.id.leaves){
            startActivity(new Intent(DashBoardActivity.this , LeaveActivity.class));
        }
        else if(id==R.id.task){
           // startActivity(new Intent(DashBoardActivity.this , ProfileActivity.class));
        }
       /* else if(id==R.id.logout){
          clearTheLocalDataAndLogout();
           }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkLocation();
        }
    }

    private void clearTheLocalDataAndLogout() {
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
        MainActivity.loginStatus = false;

        //Toast.makeText(this, "Logging out Succesfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}