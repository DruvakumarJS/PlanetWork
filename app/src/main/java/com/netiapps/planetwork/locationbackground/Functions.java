package com.netiapps.planetwork.locationbackground;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;

import com.netiapps.planetwork.R;

public class Functions {

    public static void customAlertDialog(Context context, final Callback callbackResponse) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_alert_dialouge_location);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_corner_white_bkg));

        Button okBtn = dialog.findViewById(R.id.ok_btn);
        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        okBtn.setOnClickListener(view -> {
            callbackResponse.Responce("okay");
            dialog.dismiss();
        });
        cancelBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public static void customAlertDialogDenied(Context context, final Callback callbackResponse) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_alert_dialouge_location_denied);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_corner_white_bkg));

        Button okBtn = dialog.findViewById(R.id.ok_btn);
        Button okCancel = dialog.findViewById(R.id.cancel_btn);
        okBtn.setOnClickListener(view -> {
            callbackResponse.Responce("okay");
            dialog.dismiss();
        });

        okCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
