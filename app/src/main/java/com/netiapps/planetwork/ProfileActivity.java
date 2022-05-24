package com.netiapps.planetwork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.netiapps.planetwork.utils.Constants;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgBack;
    private LinearLayout llLeave;
    private LinearLayout llReport;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private LinearLayout llLgout;
    String userName;
    String email;
    private TextView tvName;
    private TextView tvemail;
    private  TextView tvversion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilescreen);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.rel));

        sharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userName = sharedPreferences.getString(Constants.userNameKey, "");
        email = sharedPreferences.getString(Constants.email,"");

        tvName = findViewById(R.id.txt_hi);
        tvemail = findViewById(R.id.txt_profile);
        tvName.setText(" Hi, " +  userName);
        tvemail.setText(email);

        imgBack = findViewById(R.id.imgback);
        llLeave = findViewById(R.id.ll_leave);
        llReport = findViewById(R.id.llreport);
        llLgout = findViewById(R.id.lllogout);
        tvversion=findViewById(R.id.tv_version);

        String version=BuildConfig.VERSION_NAME;
        tvversion.setText("Version "+version);

        imgBack.setOnClickListener(this);
        llLeave.setOnClickListener(this);
        llLgout.setOnClickListener(this);
        llReport.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imgback:
                startActivity(new Intent(ProfileActivity.this,DashBoardActivity.class));
                finish();
                break;

            case R.id.ll_leave:
                Intent intent = new Intent(ProfileActivity.this,LeaveActivity.class);
                startActivity(intent);
                break;

            case R.id.lllogout:

                clearTheLocalDataAndLogout();
               /* Intent intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();*/
                break;

            case R.id.llreport:
                Intent intnt = new Intent(ProfileActivity.this,ReportListActivity.class);
                startActivity(intnt);
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this,DashBoardActivity.class));
        finish();
    }

    private void clearTheLocalDataAndLogout() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        MainActivity.loginStatus = false;

        //Toast.makeText(this, "Logging out Succesfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
