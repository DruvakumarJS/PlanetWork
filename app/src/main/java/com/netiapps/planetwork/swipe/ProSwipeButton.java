package com.netiapps.planetwork.swipe;

import static android.content.Context.MODE_PRIVATE;
import static com.netiapps.planetwork.swipe.Constants.BTN_INIT_RADIUS;
import static com.netiapps.planetwork.swipe.Constants.BTN_MORPHED_RADIUS;
import static com.netiapps.planetwork.swipe.Constants.DEFAULT_SWIPE_DISTANCE;
import static com.netiapps.planetwork.swipe.Constants.DEFAULT_TEXT_SIZE;
import static com.netiapps.planetwork.swipe.Constants.MORPH_ANIM_DURATION;
import static com.netiapps.planetwork.swipe.UiUtils.animateFadeHide;
import static com.netiapps.planetwork.swipe.UiUtils.animateFadeShow;
import static com.netiapps.planetwork.swipe.UiUtils.dpToPx;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.database.AppDatabase;
import com.netiapps.planetwork.database.DbModelSendingData;
import com.netiapps.planetwork.database.UserLoginData;
import com.netiapps.planetwork.services.GpsTracker;
import com.netiapps.planetwork.services.LocationService;
import com.netiapps.planetwork.services.LoginTimeCounterService;
import com.netiapps.planetwork.utils.LocalHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by shadow-admin on 24/10/17.
 */

public class ProSwipeButton extends RelativeLayout {

    private Context context;
    private View view;
    private GradientDrawable gradientDrawable;
    private RelativeLayout contentContainer;
    //    private TextView contentTv;
    private ImageView arrow1;
    private ImageView arrow2;
    private LinearLayout arrowHintContainer;
    private LinearLayout forwardArrowContainer;
    private ProgressBar progressBar;
    private TextView contentTv1;
    private TextView contentTv2;
    private TextView contentTv3;
    private TextView contentTv4;

    private int CLICK_ACTION_THRESHOLD = 200;

    private LinearLayout llLoginTimer;

    //// TODO: 26/10/17 Add touch blocking

    private float initialTouchX;
    private float initialTouchY;
    private long lastTouchDown;
    private static int CLICK_ACTION_THRESHHOLD = 100;

    /*
        User configurable settings
     */

    private CharSequence btnText = "BUTTON";
    private CharSequence btnText1 = "Slide left to Logout";
    private CharSequence btnText2 = "00:00:00";
    private CharSequence btnText3 = "Slide Right to Login";
    private CharSequence btnText4 = "00:00:00";

    private String saved_login_time="00:00:00";

    @ColorInt
    private int textColorInt;
    @ColorInt
    private int bgColorInt;
    @ColorInt
    private int arrowColorInt;
    private float btnRadius = BTN_INIT_RADIUS;
    @Dimension
    private float textSize = DEFAULT_TEXT_SIZE;
    @Nullable
    private OnSwipeListener swipeListener = null;

    @Nullable
    private LoginStartStopListener loginStartStopListener = null;
    private float swipeDistance = DEFAULT_SWIPE_DISTANCE;
    private LinearLayout backArrowContainer;
    private LinearLayout llLogoutTimer;
    private LinearLayout linearLayoutSwipeBtnHintContainerLeft;
    private ImageView pause;
    private ImageView ivArrow3;
    private ImageView ivArrow4;
    private boolean reverse;
    Handler handler = new Handler();
    private boolean isPauseClicked = true;
    float layoutXAxis;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private CountDownTimer countDownTimer;
    private boolean mTimerRunning;

    long initialTime;

    int setSec= 0;
    int setMin = 0;
    int setHr = 0;
    int tempPause = 0;
    boolean check = false;
    int hour;
    int days;
    int millisec=1000;
   int logouttime=9*60*60;
//    int logouttime=1*60;
   // int saturation_time=2*61;
   // int logouttime= 62 ;
    int  updated_seconds;
    BroadcastReceiver broadcastReceiver;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, MilliSeconds ;
    String time;
    private int seconds = 0;
    private  String startPlay;
    private String pausePlay;
    private String stopPlay;

    Intent intent ;
    private GpsTracker gpsTracker;

    public ProSwipeButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ProSwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(context, attrs);
        init();
    }

    public ProSwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(context, attrs);
        init();
    }

    private void setAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProSwipeButton,
                0, 0);
        try {
            final String btnString = a.getString(R.styleable.ProSwipeButton_btn_text);
            if (btnString != null)
                btnText = btnString;
            textColorInt = a.getColor(R.styleable.ProSwipeButton_text_color, ContextCompat.getColor(context, android.R.color.white));
            bgColorInt = a.getColor(R.styleable.ProSwipeButton_bg_color, ContextCompat.getColor(context, R.color.proswipebtn_planet_work));
//            arrowColorInt = a.getColor(R.styleable.ProSwipeButton_arrow_color, ContextCompat.getColor(context, R.color.proswipebtn_translucent_white));
            arrowColorInt = a.getColor(R.styleable.ProSwipeButton_arrow_color, ContextCompat.getColor(context, R.color.proswipebtn_translucent_white));

            btnRadius = a.getFloat(R.styleable.ProSwipeButton_btn_radius, BTN_INIT_RADIUS);
            textSize = a.getDimensionPixelSize(R.styleable.ProSwipeButton_text_size, (int) DEFAULT_TEXT_SIZE);
            reverse = a.getBoolean(R.styleable.ProSwipeButton_reverse, false);

            intent = new Intent(context, LocationService.class);


        } finally {
            a.recycle();
        }
    }



    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.deumo, this, true);

      /*  IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");*/
     /*    broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.v("TAG","br action is "+action);
                reverse=false;
                show_loggedin_layout();
            }
        };*/
       // context.registerReceiver(broadcastReceiver, new IntentFilter("updatetext"));

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Log.v("TAG","Swipe layout activated");
        contentContainer = view.findViewById(R.id.relativeLayout_swipeBtn_contentContainer);
       // arrowHintContainer = view.findViewById(R.id.linearLayout_swipeBtn_hintContainer);
        arrowHintContainer = view.findViewById(R.id.linearLayout_swipeBtn);
        llLoginTimer = view.findViewById(R.id.ll_login_timer);

        linearLayoutSwipeBtnHintContainerLeft  = view.findViewById(R.id.linearLayout_swipeBtnLeft);
        llLogoutTimer = view.findViewById(R.id.ll_logout_timer);
        pause = view.findViewById(R.id.pause);
        backArrowContainer = view.findViewById(R.id.linearLayout_swipeBtn_hintContainerLeft);
        ivArrow3 = view.findViewById(R.id.iv_arrow3);
        ivArrow4 = view.findViewById(R.id.iv_arrow4);

        forwardArrowContainer = view.findViewById(R.id.linearLayout_swipeBtn_hintContainer);

        contentTv1 = view.findViewById(R.id.tv_btnText1);
        contentTv2 = view.findViewById(R.id.tv_btnText2);
        contentTv3 = view.findViewById(R.id.tv_btnText3);
        contentTv4 = view.findViewById(R.id.tv_btnText4);

        arrow1 = view.findViewById(R.id.iv_arrow1);
        arrow2 = view.findViewById(R.id.iv_arrow2);

        tintArrowHint();
//        contentTv.setText(btnText);
//        contentTv.setTextColor(textColorInt);
//        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        sharedPreferences = (SharedPreferences) context.getSharedPreferences(com.netiapps.planetwork.utils.Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        saved_login_time=sharedPreferences.getString("login_strat_time","00:00");
        String force_stop_timer=sharedPreferences.getString("force_stop_timer","");

        updated_seconds=sharedPreferences.getInt("total_seconds",0);

        String mm=sharedPreferences.getString("session_loggedin","0");

        if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1"))
        {
            Log.v("TAG","Timer Started due to login");
            long secoundss=0;
            if(updated_seconds==0){
                 secoundss=updated_seconds;
            }
            else {
                 secoundss=updated_seconds-1;
            }

            long hr = secoundss / 3600;
            long Min = (secoundss % 3600) / 60;
            long Sec = secoundss % 60;

            String updatedtime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, Min, Sec);

                time = updatedtime;
                btnText4 = updatedtime;
                btnText2 = updatedtime;

                reverse=true;

                //        handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 0);

                show_loggedin_layout();


        }

        else {
            mEditor.putString("session_loggedin","0");
            mEditor.putString("login_strat_time", "00:00:00");
            mEditor.putInt("total_seconds", 0);
            mEditor.apply();
        }


        contentTv1.setText(btnText1);
        contentTv2.setText(btnText2);
        contentTv3.setText(btnText3);
        contentTv4.setText(btnText4);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(btnRadius);
        setBackgroundColor(bgColorInt);
        updateBackground();

        if(reverse){
            setupTouchListenerReverse();
        }else{
            setupTouchListener();
        }

        //  pauseOnClick();
        //  pauseOnClick2();
    }

    private void show_loggedin_layout() {

        animateFadeShow(context, llLogoutTimer);
       // animateFadeHide(context, llLoginTimer);
        animateFadeShow(context, backArrowContainer);
        animateFadeHide(context, forwardArrowContainer);
        animateFadeHide(context,arrowHintContainer);

        animateFadeShow(context, contentTv1);
        animateFadeShow(context, contentTv2);
        animateFadeHide(context,contentTv3);
        animateFadeHide(context,contentTv4);
        animateFadeShow(context, linearLayoutSwipeBtnHintContainerLeft);

        layoutXAxis = linearLayoutSwipeBtnHintContainerLeft.getX();
        linearLayoutSwipeBtnHintContainerLeft.setX(getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());


        startBackAnim();

        if(reverse){
            setupTouchListenerReverse();
        }else{
            setupTouchListener();
        }

    }

    public void updateStatus(String status){
        mEditor.putString("StartPlayOn",status);
        mEditor.commit();
        Log.d("StartPlayOn",status);
        // timeCalcultaion();
    }


    public static void  oneLoop(){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()- startTime<1);
    }



    private void setupTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Movement logic here
                        if (event.getX() > arrowHintContainer.getWidth() / 2 &&
                                event.getX() + arrowHintContainer.getWidth() / 2 < getWidth() &&
                                (event.getX() < arrowHintContainer.getX() + arrowHintContainer.getWidth() || arrowHintContainer.getX() != 0)) {
                            // snaps the hint to user touch, only if the touch is within hint width or if it has already been displaced
                            arrowHintContainer.setX(event.getX() - arrowHintContainer.getWidth() / 2);
                        }

                        if (arrowHintContainer.getX() + arrowHintContainer.getWidth() > getWidth() &&
                                arrowHintContainer.getX() + arrowHintContainer.getWidth() / 2 < getWidth()) {
                            // allows the hint to go up to a max of btn container width
                            arrowHintContainer.setX(getWidth() - arrowHintContainer.getWidth());
                        }

                        if (event.getX() < arrowHintContainer.getWidth() / 2 &&
                                arrowHintContainer.getX() > 0) {
                            // allows the hint to go up to a min of btn container starting
                            arrowHintContainer.setX(0);
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        //Release logic here

                        if (arrowHintContainer.getX() + arrowHintContainer.getWidth() > getWidth() * swipeDistance) {
                            // swipe completed, fly the hint away!
                            performSuccessfulSwipe();
                        } else if (arrowHintContainer.getX() <= 0) {
                            // upon click without swipe
                            startFwdAnim();
                        } else {
                            // swipe not completed, pull back the hint
                            animateHintBack();
                        }
                        return true;
                }

                return false;
            }
        });
    }

    private void performSuccessfulSwipe() {
            if (swipeListener != null)
            swipeListener.onSwipeConfirm();
        reverse = true;
        morphToCircle();
        updateStatus("1");

        String curent_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        mEditor.putString("session_loggedin","1");
        mEditor.putString("login_time",curent_time);

        mEditor.putString("forcestoptimer","0");
        mEditor.putString("timer_paused","false");
        mEditor.putInt("total_seconds",0);

        Log.v("DR","login time"+curent_time);

        mEditor.apply();

        if (loginStartStopListener != null)
            loginStartStopListener.startStop("LOGIN");
        Log.v("TAG","session loGIN ");

        if(LocalHelper.isConnectedToInternet(context)) {
            //LocalHelper.call_attendednce_api("login", context);
            insertDataToRoomDB("login");
        }
        else {

            insertDataToRoomDB("login");

        }
        context.startService(intent);

        if (isMyServiceRunning(LoginTimeCounterService.class)) {

        }
        else
        {
            Intent intent = new Intent(context, LoginTimeCounterService.class);
            context.startService(intent);
            Log.i("DB","LoginTimeCounterService is started");

        }


    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    private void performSuccessfulSwipeReverse() {
        if (swipeListener != null)
            swipeListener.onSwipeConfirm();

        setPauseAndPlayDrawable(false);


        isPauseClicked = true;
        //handler.postDelayed(runnable,0);

        reverse = false;
        handler.removeCallbacks(runnable);
        contentTv2.setText("00:00:00");
        seconds = 0;
        hour =0;
        Minutes =0;
        morphToCircleReverse();
        updateStatus("0") ;

        if (loginStartStopListener != null)
            loginStartStopListener.startStop("LOGOUT");
        mEditor.putBoolean(com.netiapps.planetwork.utils.Constants.REVERSE_FIRST_TIME,false);
        Log.v("TAG","session loGOUT ");


        mEditor.putInt("total_seconds", 0);
        mEditor.putString("session_loggedin","0");
        mEditor.putString("ot_started","false");
        mEditor.putString("forcestoptimer","0");
        mEditor.putString("timer_paused","false");
     //   mEditor.putString("login_time","00");
        mEditor.putBoolean("reset_timer",true);
        mEditor.apply();

        context.startService(intent);

       // LocalHelper.call_attendednce_api("logout",context);
        if(LocalHelper.isConnectedToInternet(context)) {
            // LocalHelper.call_attendednce_api("login", context);
            insertDataToRoomDB("logout");
        }
        else {
            insertDataToRoomDB("logout");

        }
        setupTouchListener();

    }

    public void morphToCircleReverse() {

        animateFadeHide(context, linearLayoutSwipeBtnHintContainerLeft);
        animateFadeHide(context, backArrowContainer);


        setOnTouchListener(null);
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", BTN_INIT_RADIUS, BTN_MORPHED_RADIUS);

        animateFadeHide(context, contentTv3);
        animateFadeHide(context, contentTv4);

        ValueAnimator widthAnimation;
        widthAnimation = ValueAnimator.ofInt(getWidth(), dpToPx(50));
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.width = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimation = ValueAnimator.ofInt(getHeight(), dpToPx(50));
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.height = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(MORPH_ANIM_DURATION);
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        animatorSet.start();

        showProgressBar();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (reverse) {
            startBackAnim();
        } else {
            startFwdAnim();
        }
    }

    private void animateHintBack() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(arrowHintContainer.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                arrowHintContainer.setX(x);
            }
        });

        positionAnimator.setDuration(200);
        positionAnimator.start();
    }


    private void animateHintBackReverse() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(linearLayoutSwipeBtnHintContainerLeft.getX(), getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                linearLayoutSwipeBtnHintContainerLeft.setX(x);
            }
        });

        positionAnimator.setDuration(200);
        positionAnimator.start();
    }

    private void startFwdAnim() {
        if (isEnabled()) {
            TranslateAnimation animation = new TranslateAnimation(0, getMeasuredWidth(), 0, 0);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startHintInitAnim();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            forwardArrowContainer.startAnimation(animation);
        }
    }


    private void startBackAnim() {
        if (isEnabled()) {
            TranslateAnimation animation = new TranslateAnimation(0, -getMeasuredWidth(), 0, 0);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.setDuration(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startHintInitAnimReverse();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backArrowContainer.startAnimation(animation);
        }
    }

    /**
     * animate entry of hint from the left-most edge
     */
    private void startHintInitAnim() {
        TranslateAnimation anim = new TranslateAnimation(-arrowHintContainer.getWidth(), 0, 0, 0);
        anim.setDuration(500);
        arrowHintContainer.startAnimation(anim);
    }


    /**
     * animate entry of hint from the left-most edge
     */
    private void startHintInitAnimReverse() {
        TranslateAnimation anim = new TranslateAnimation( linearLayoutSwipeBtnHintContainerLeft.getWidth() * 2 , 0, 0, 0);
        anim.setDuration(500);
        linearLayoutSwipeBtnHintContainerLeft.startAnimation(anim);
    }


    /**
     * Just like performOnClick() in a standard button,
     * this will call the attached OnSwipeListener
     * and morph the btn to a circle
     */
    public void performOnSwipe() {
        performSuccessfulSwipe();
    }

    public void morphToCircle() {
        animateFadeHide(context, arrowHintContainer);
        animateFadeHide(context, forwardArrowContainer);


        setOnTouchListener(null);
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", BTN_INIT_RADIUS, BTN_MORPHED_RADIUS);

        animateFadeHide(context, contentTv1);
        animateFadeHide(context, contentTv2);
        animateFadeHide(context, contentTv3);
        animateFadeHide(context, contentTv4);

        ValueAnimator widthAnimation;
        widthAnimation = ValueAnimator.ofInt(getWidth(), dpToPx(50));
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.width = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimation = ValueAnimator.ofInt(getHeight(), dpToPx(50));
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.height = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(MORPH_ANIM_DURATION);
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        animatorSet.start();

        showProgressBar();
    }

    private void morphToRect() {
        if(reverse){
            setupTouchListenerReverse();
        }else{
            setupTouchListener();
        }
        //pauseOnClick();
        ObjectAnimator cornerAnimation =
                ObjectAnimator.ofFloat(gradientDrawable, "cornerRadius", BTN_MORPHED_RADIUS, BTN_INIT_RADIUS);

        ValueAnimator widthAnimation;
        widthAnimation = ValueAnimator.ofInt(dpToPx(50), getWidth());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.width = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimation = ValueAnimator.ofInt(dpToPx(50), getWidth());
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
                layoutParams.height = val;
                contentContainer.setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(MORPH_ANIM_DURATION);
        animatorSet.playTogether(cornerAnimation, widthAnimation, heightAnimation);
        animatorSet.start();
    }

    public void updateBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentContainer.setBackground(gradientDrawable);
        } else {
            // noinspection deprecation
            contentContainer.setBackgroundDrawable(gradientDrawable);
        }
    }


    private void  setupTouchListenerReverse() {
        int CLICK_ACTION_THRESHOLD = 200;
        final float[] startX = {0f};
        final float[] startY = new float[1];

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX[0] = event.getX();
                        startY[0] = event.getY();

                        lastTouchDown = System.currentTimeMillis();

                        initialTouchX = event.getX();
                        initialTouchY = event.getY();


                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Movement logic here

                        if ( event.getX() > linearLayoutSwipeBtnHintContainerLeft.getWidth() / 2 &&
                                event.getX() + linearLayoutSwipeBtnHintContainerLeft.getWidth() / 2 < getWidth() &&
                                (event.getX() > linearLayoutSwipeBtnHintContainerLeft.getX() || linearLayoutSwipeBtnHintContainerLeft.getX() != getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth())) {
                            // snaps the hint to user touch, only if the touch is within hint width or if it has already been displaced
                            linearLayoutSwipeBtnHintContainerLeft.setX(event.getX() - linearLayoutSwipeBtnHintContainerLeft.getWidth() / 2);
                        }

                        if (linearLayoutSwipeBtnHintContainerLeft.getX() + linearLayoutSwipeBtnHintContainerLeft.getWidth() > getWidth() &&
                                linearLayoutSwipeBtnHintContainerLeft.getX() + linearLayoutSwipeBtnHintContainerLeft.getWidth() / 2 < getWidth()) {
                            // allows the hint to go up to a max of btn container width
                            linearLayoutSwipeBtnHintContainerLeft.setX(getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());
                        }

                        if (linearLayoutSwipeBtnHintContainerLeft.getX() < linearLayoutSwipeBtnHintContainerLeft.getWidth() / 2 &&
                                linearLayoutSwipeBtnHintContainerLeft.getX() > 0) {
                            // allows the hint to go up to a min of btn container starting
                            linearLayoutSwipeBtnHintContainerLeft.setX(0);
                        }


                        return true;
                    case MotionEvent.ACTION_UP:


                        float endX = event.getX();
                        float endY = event.getY();


                        String x = linearLayoutSwipeBtnHintContainerLeft.getX() + "" ;
                        String y = getWidth() + "" ;
                        String z = linearLayoutSwipeBtnHintContainerLeft.getX() - getWidth() + "" ;
                        String u = event.getX() + "";
                        String a = getWidth() - (getWidth() - linearLayoutSwipeBtnHintContainerLeft.getX())+ "";
                        String g = layoutXAxis + "";
                        String i = linearLayoutSwipeBtnHintContainerLeft.getWidth() + "";
                        String d = (linearLayoutSwipeBtnHintContainerLeft.getX() >= getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth()) + "";
                        float l = (linearLayoutSwipeBtnHintContainerLeft.getX() + linearLayoutSwipeBtnHintContainerLeft.getWidth()/2);
                        float o = (linearLayoutSwipeBtnHintContainerLeft.getY() + linearLayoutSwipeBtnHintContainerLeft.getHeight()/2);

                        float differenceX = Math.abs(endX - l);
                        float differenceY = Math.abs(endY - o);

                        if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD &&  (differenceX < 40 && differenceY < 40))  {
                            if(isPauseClicked){

                                mEditor.putString("timer_paused","true");
                                mEditor.putString("forcestoptimer","0");

                                mEditor.putBoolean("ispause",true);
                                mEditor.apply();

                                context.stopService(intent);
                                setPauseAndPlayDrawable(true);
                                linearLayoutSwipeBtnHintContainerLeft.setX(getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());
                                isPauseClicked = false;
                                handler.removeCallbacks(runnable);
                                updateStatus("2");
                                //      getLocation();

                                if (loginStartStopListener != null)
                                    loginStartStopListener.startStop("PAUSE");

                                Log.v("TAG","session loGOUT ");
                               // LocalHelper.call_attendednce_api("logout",context);
                                if(LocalHelper.isConnectedToInternet(context)) {
                                    // LocalHelper.call_attendednce_api("login", context);
                                    insertDataToRoomDB("logout");
                                }
                                else {
                                    insertDataToRoomDB("logout");

                                }


                                //  startBackAnim();
                            }else{

                                mEditor.putString("timer_paused","false");
                                mEditor.putString("forcestoptimer","0");

                                mEditor.putBoolean("ispause",false);
                                mEditor.apply();
                                reverse=true;

                                setPauseAndPlayDrawable(false);
                                linearLayoutSwipeBtnHintContainerLeft.setX(getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());
                                isPauseClicked = true;
                                updateStatus("1");
                                handler.postDelayed(runnable,0);
                                //   getLocation();
                                context.startService(intent);
                                if (loginStartStopListener != null)
                                    loginStartStopListener.startStop("RESUME");
                                // startBackAnim();

                                Log.v("TAG","session loGIN ");
                               // LocalHelper.call_attendednce_api("login",context);
                                if(LocalHelper.isConnectedToInternet(context)) {
                                    // LocalHelper.call_attendednce_api("login", context);
                                    insertDataToRoomDB("login");
                                }
                                else {
                                    insertDataToRoomDB("login");

                                }
                            }
                            return true;
                        }

                        if(linearLayoutSwipeBtnHintContainerLeft.getX() < getWidth() - (getWidth() * swipeDistance)){
                            performSuccessfulSwipeReverse();
                        }else if(linearLayoutSwipeBtnHintContainerLeft.getX() >= getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth()){
                            startBackAnim();
                        }else{
                            animateHintBackReverse();

                        }

                        return true;
                }

                return false;
            }
        });
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private boolean isAClickOld(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > 5/* =5 */ || differenceY > 5);
    }

    private void setPauseAndPlayDrawable(boolean isPause){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pause.setImageDrawable(getResources().getDrawable(isPause ? R.drawable.play_button : R.drawable.pause_button, context.getApplicationContext().getTheme()));
        } else {
            pause.setImageDrawable(getResources().getDrawable(isPause ? R.drawable.play_button : R.drawable.pause_button));
        }
    }



    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            gradientDrawable.setColor(ContextCompat.getColor(context, R.color.proswipebtn_disabled_grey));
            updateBackground();
            this.setAlpha(0.5f);
        } else {
            setBackgroundColor(getBackgroundColor());
            this.setAlpha(1f);
        }
    }

    private void showProgressBar() {
        progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_IN);
        animateFadeHide(context, contentTv1);
        animateFadeHide(context, contentTv2);
        animateFadeHide(context, contentTv3);
        animateFadeHide(context, contentTv4);


        contentContainer.addView(progressBar);
    }

    public void showResultIcon(boolean isSuccess, boolean shouldReset) {
        animateFadeHide(context, progressBar);

        final AppCompatImageView failureIcon = new AppCompatImageView(context);
        LayoutParams icLayoutParams =
                new LayoutParams(dpToPx(50), dpToPx(50));
        failureIcon.setLayoutParams(icLayoutParams);
        failureIcon.setVisibility(GONE);
        int icon;
        if (isSuccess)
            icon = R.drawable.ic_check_circle_36dp;
        else
            icon = R.drawable.ic_cancel_full_24dp;
        failureIcon.setImageResource(icon);
        contentContainer.addView(failureIcon);
        animateFadeShow(context, failureIcon);

        if (shouldReset) {
            // expand the btn again
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateFadeHide(context, failureIcon);
                    morphToRect();

                }
            }, 1000);
        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateFadeHide(context, failureIcon);
                    morphToRect();


                    //                   hideForwardLayout();
                    //                   showReverseLayout();

                    if(reverse){
                        handler.postDelayed(runnable, 0);
                        showReverseLayout();
                    }else{

                        showForwardLayout();
                    }


                }
            }, 1000);
        }
    }

    private void showReverseLayout(){
        Log.w("TAG","Revrse called");
        reverse = true;
        if(sharedPreferences.getBoolean(com.netiapps.planetwork.utils.Constants.REVERSE_FIRST_TIME, false)){
            linearLayoutSwipeBtnHintContainerLeft.setX(0);
          /*  mEditor.putBoolean(com.netiapps.planetwork.utils.Constants.REVERSE_FIRST_TIME,false);
            mEditor.apply();*/
        }else{
            linearLayoutSwipeBtnHintContainerLeft.setX(getWidth() - linearLayoutSwipeBtnHintContainerLeft.getWidth());
        }

        animateFadeShow(context, llLogoutTimer);
        animateFadeShow(context, backArrowContainer);
        animateFadeShow(context, contentTv1);
        animateFadeShow(context, contentTv2);

        animateFadeShow(context, linearLayoutSwipeBtnHintContainerLeft);


        layoutXAxis = linearLayoutSwipeBtnHintContainerLeft.getX();
        startBackAnim();

        setupTouchListenerReverse();







    }

    private void hideForwardLayout2(){
        morphToRect();
        arrowHintContainer.setX(0);
        animateFadeShow(context, arrowHintContainer);
        animateFadeShow(context,forwardArrowContainer);
        animateFadeShow(context, contentTv1);
        animateFadeShow(context, contentTv2);
        animateFadeShow(context, contentTv3);
        animateFadeShow(context, contentTv4);


    }

    private void showForwardLayout(){
        reverse = false;
        arrowHintContainer.setX(0);
        animateFadeShow(context, arrowHintContainer);
        animateFadeShow(context,forwardArrowContainer);
        animateFadeShow(context, contentTv3);
        animateFadeShow(context, contentTv4);

       logout_settings();

    }

    private void logout_settings() {
        seconds=0;
        contentTv4.setText("00:00:00");
        contentTv2.setText("00:00:00");

        mEditor.putString("session_loggedin","0");
        mEditor.putString("timer_paused","false");

        mEditor.putInt("total_seconds", 0);
        mEditor.apply();

        handler.removeCallbacks(runnable);
        time = String.format(Locale.getDefault(), "%02d:%02d:%02d", 00, 00, 00);

        contentTv2.setText("00:00:00");
        contentTv4.setText("00:00:00");
        seconds=0;
        reverse = false;
       // performSuccessfulSwipeReverse();
    }

    private void hideForwardLayout(){
        animateFadeHide(context, forwardArrowContainer);
        animateFadeHide(context, llLoginTimer);
        animateFadeHide(context, arrowHintContainer);

        animateFadeShow(context, contentTv1);
        animateFadeShow(context, contentTv2);
    }


    public void showResultIcon(boolean isSuccess) {
        showResultIcon(isSuccess, !isSuccess);
    }

    private void tintArrowHint() {
        arrow1.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);
        arrow2.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);

        ivArrow3.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);
        ivArrow4.setColorFilter(arrowColorInt, PorterDuff.Mode.MULTIPLY);

    }

    public void onDestroyView() {
        handler.removeCallbacks(runnable);
        //context.unregisterReceiver(broadcastReceiver);
    }
    public void forcestoptimer() {
       reverse=false;
    }

    public interface OnSwipeListener {
        void onSwipeConfirm();
    }

    public interface LoginStartStopListener {
        void startStop(String status);
    }

    public void setText(CharSequence text) {
        this.btnText1 = text;
        contentTv1.setText(text);
    }

    public void shiftLeft(){

    }


    public void setTextForLogin(CharSequence text) {
        this.btnText4 = text;
        contentTv4.setText(text);
    }


    public void setTextForLogout(CharSequence text) {
        this.btnText2 = text;
        contentTv2.setText(text);
    }


    public CharSequence getText() {
        return this.btnText;
    }


    public CharSequence getTextLogin() {
        return this.btnText4;
    }


    public CharSequence getTextLogout() {
        return this.btnText2;
    }

    @ColorInt
    public int getTextColor() {
        return this.textColorInt;
    }

    public void setBackgroundColor(@ColorInt int bgColor) {
        this.bgColorInt = bgColor;
        gradientDrawable.setColor(bgColor);
        updateBackground();
    }

    @ColorInt
    public int getBackgroundColor() {
        return this.bgColorInt;
    }

    public void setCornerRadius(float cornerRadius) {
        this.btnRadius = cornerRadius;
    }

    public float getCornerRadius() {
        return this.btnRadius;
    }

    public int getArrowColorRes() {
        return this.arrowColorInt;
    }

    /**
     * Include alpha in arrowColor for transparency (ex: #33FFFFFF)
     */
    public void setArrowColor(int arrowColor) {
        this.arrowColorInt = arrowColor;
        tintArrowHint();
    }

//    public void setTextSize(@Dimension float textSize) {
//        this.textSize = textSize;
//        contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//    }

    @Dimension
    public float getTextSize() {
        return this.textSize;
    }

    /**
     * How much of the button must the user swipe to trigger the OnSwipeListener successfully
     *
     * @param swipeDistance float from 0.0 to 1.0 where 1.0 means user must swipe the button fully from end to end. Default is 0.85.
     */
    public void setSwipeDistance(@Dimension float swipeDistance) {
        if (swipeDistance > 1.0f) {
            swipeDistance = 1.0f;
        }
        if (swipeDistance < 0.0f) {
            swipeDistance = 0.0f;
        }
        this.swipeDistance = swipeDistance;
    }

    @Dimension
    public float getSwipeDistance() {
        return this.swipeDistance;
    }

    public void setOnSwipeListener(@Nullable OnSwipeListener customSwipeListener) {
        this.swipeListener = customSwipeListener;
    }

    public void startStop(@Nullable LoginStartStopListener loginStartStopListener) {
        this.loginStartStopListener = loginStartStopListener;
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(context);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            DbModelSendingData task = new DbModelSendingData();
            task.setUserempId(sharedPreferences.getString(com.netiapps.planetwork.utils.Constants.userIdKey, ""));
            task.setLat(String.valueOf(latitude));
            task.setLng(String.valueOf(longitude));
            task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
            task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
            task.setStatus(sharedPreferences.getString("StartPlayOn",""));

            AppDatabase db = AppDatabase.getDbInstance(context);
            db.taskDao().insert(task);


//            SaveTask st = new SaveTask();
//            st.execute(latitude,longitude);
        }else{
            // showSettingsAlert(tvlat,tvlon);
        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            int total_seconds=sharedPreferences.getInt("total_seconds",0);
            String curent_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            seconds=total_seconds;
            hour = seconds / 3600;
            Minutes = (seconds % 3600) / 60;
            Seconds = seconds % 60;

            if(sharedPreferences.getString("timer_paused","0").equalsIgnoreCase("true"))
            {
              //  Log.w("TAG","case 1");
                reverse=false;
                setPauseAndPlayDrawable(true);
            }

            if (reverse) {

                time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, Minutes, Seconds);
                contentTv2.setText(time);
                contentTv4.setText(time);

                mEditor.putString("session_loggedin","1");
             //   Log.v("TAG","login_duration_seconds"+total_seconds);
                seconds++;

            }
            else
            {
               // Log.w("TAG","case 3");
                contentTv2.setText(time);
                contentTv4.setText(time);
            }

            handler.postDelayed(this, millisec);

            if ((seconds>=logouttime && !sharedPreferences.getBoolean("insideGeofence",false)) ||
            sharedPreferences.getBoolean("session_end",false))
          //  if (seconds>=logouttime)
            {
                handler.removeCallbacks(runnable);
                time = String.format(Locale.getDefault(), "%02d:%02d:%02d", 00, 00, 00);

                contentTv2.setText("00:00:00");
                contentTv4.setText("00:00:00");
                seconds=0;
                reverse = false;
               // performSuccessfulSwipeReverse();
                ShowAutologoutlayout();

                mEditor.putString("session_loggedin","0");
                mEditor.putString("timer_paused","false");
                mEditor.putString("ot_started","false");

            }

            //mEditor.putString("login_strat_time", time);
            mEditor.putString("time_at_destroy",curent_time);
            mEditor.putInt("total_seconds", seconds);

            mEditor.apply();

        }

    };

    private void ShowAutologoutlayout() {

        if (swipeListener != null)
            swipeListener.onSwipeConfirm();

        setPauseAndPlayDrawable(false);
      //  animateFadeShow(context, llLoginTimer);
        animateFadeHide(context, llLogoutTimer);
        isPauseClicked = true;
        //handler.postDelayed(runnable,0);

        reverse = false;
        handler.removeCallbacks(runnable);
        contentTv2.setText("00:00:00");
        seconds = 0;
        hour =0;
        Minutes =0;
        morphToCircleReverse();
        updateStatus("0") ;

        if (loginStartStopListener != null)
            loginStartStopListener.startStop("LOGOUT");

        mEditor.putBoolean(com.netiapps.planetwork.utils.Constants.REVERSE_FIRST_TIME,false);
        mEditor.putInt("total_seconds", 0);
        mEditor.putBoolean("reset_timer",true);

        mEditor.putString("forcestoptimer","0");
        mEditor.putString("timer_paused","false");
        mEditor.apply();

        context.startService(intent);

        setupTouchListener();

        if(LocalHelper.isConnectedToInternet(context)) {
            // LocalHelper.call_attendednce_api("login", context);
            insertDataToRoomDB("logout");
        }
        else {
            insertDataToRoomDB("logout");

        }


        /*animateFadeHide(context, backArrowContainer);
        animateFadeShow(context, forwardArrowContainer);
        animateFadeShow(context,arrowHintContainer);

        animateFadeHide(context, contentTv1);
        animateFadeHide(context, contentTv2);
        animateFadeShow(context,contentTv3);
        animateFadeShow(context,contentTv4);*/

      //  layoutXAxis = linearLayoutSwipeBgetX()tnHintContainerLeft.getX();

       // startBackAnim();
    }


    public void timeCalcultaion(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");

        try {
            Date date1 = simpleDateFormat.parse("03:12");
            Date date2 = simpleDateFormat.parse("03:15");

            long difference = date2.getTime() - date1.getTime();
            days = (int) (difference / (1000*60*60*24));
            hour = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            Minutes = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hour)) / (1000*60);
            hour = (hour < 0 ? -hour : hour);
            Log.i("======= Hours"," :: "+hour + Minutes);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
        }
    }

    class SaveTask extends AsyncTask<Double, Double, Void> {


        @Override
        protected Void doInBackground(Double... doubles) {

            //creating a task
            DbModelSendingData task = new DbModelSendingData();
            task.setUserempId(sharedPreferences.getString(com.netiapps.planetwork.utils.Constants.userIdKey, ""));
            task.setLat(String.valueOf(doubles[0]));
            task.setLng(String.valueOf(doubles[1]));
            task.setDate(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), true));
            task.setTime(getDateWithFormat(Calendar.getInstance().getTimeInMillis(), false));
            task.setStatus(sharedPreferences.getString("StartPlayOn",""));

            //adding to database
            AppDatabase.getDbInstance(context)
                    .taskDao()
                    .insert(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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

    private void starttimerwithoutlimit() {
        Log.v("TAG","Timer Started due to OT");
        long secoundss=0;
        if(updated_seconds==0){
            secoundss=updated_seconds;
        }
        else {
            secoundss=updated_seconds-1;
        }

        long hr = secoundss / 3600;
        long Min = (secoundss % 3600) / 60;
        long Sec = secoundss % 60;

        String updatedtime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, Min, Sec);

        time = updatedtime;
        btnText4 = updatedtime;
        btnText2 = updatedtime;

        reverse=true;

        //        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 0);
       // linearLayoutSwipeBtnHintContainerLeft.setX(0);
        linearLayoutSwipeBtnHintContainerLeft.setY(0);
        show_loggedin_layout();
       /* if(reverse){
            setupTouchListenerReverse();
        }else{
            setupTouchListener();
        }*/

    }

    public void starttimer(Context context){
        //Log.v("TAG","kkkkk");
        mEditor.putString("session_loggedin","1");
        mEditor.apply();
        starttimerwithoutlimit();

    }
    private void insertDataToRoomDB(String type) {

        String date =new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Random r = new Random();
        int randomNumber = r.nextInt(999999);

        if(type.equalsIgnoreCase("login")) {

            UserLoginData logindata = new UserLoginData();
            logindata.setDate(date);
            logindata.setLogin_time(time);
            logindata.setLogout_time("0");
            logindata.setUser_id(sharedPreferences.getString(com.netiapps.planetwork.utils.Constants.userIdKey, ""));
            logindata.setLoginID(String.valueOf(randomNumber));

            AppDatabase db = AppDatabase.getDbInstance(context);
            db.logintaskDao().insert(logindata);
            Log.i("DB", "Updated User Login Data");
            mEditor.putString("DBloginID", String.valueOf(randomNumber));
            mEditor.apply();
        }
        else {
            String loginID=sharedPreferences.getString("DBloginID","");
            AppDatabase db = AppDatabase.getDbInstance(context);
            db.logintaskDao().update(time,loginID);
            Log.i("DB", "Updated User Logout Data : "+loginID);
            mEditor.putString("DBloginID", "0");
            mEditor.apply();
        }

    }
}
