package com.netiapps.planetwork.rough;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.broadcast.SendLocationToServerBroadcastReceiver;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button alarmbutton, cancelButton;
    EditText text;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;

    int  ELAPSED_TIME_TO_SAVE_IN_SERVER = 10 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_extra);
        alarmbutton = (Button) findViewById(R.id.button);
        cancelButton = (Button) findViewById(R.id.button2);
        text = (EditText) findViewById(R.id.editText);
        alarmbutton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        /* use this method if you want to start Alarm at particular time*/

        // startAlertAtParticularTime();

    }

    // This method to be called at Start button click and set repeating at every 10 seconds interval

    public void startAlert(View view) {
        if (!text.getText().toString().equals("")) {
            int i = Integer.parseInt(text.getText().toString());
            intent = new Intent(this, SendLocationToServerBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + (i * 1000), ELAPSED_TIME_TO_SAVE_IN_SERVER
                    , pendingIntent);

            Toast.makeText(this, "Alarm will set in " + i + " seconds",
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Please Provide time ",Toast.LENGTH_SHORT).show();
        }

    }

    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval

        intent = new Intent(this, SendLocationToServerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 40);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, pendingIntent);

        Toast.makeText(this, "Alarm will vibrate at time specified",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            startAlert(v);
        } else {
            if (alarmManager != null) {

                alarmManager.cancel(pendingIntent);
                Toast.makeText(this, "Alarm Disabled !!",Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
