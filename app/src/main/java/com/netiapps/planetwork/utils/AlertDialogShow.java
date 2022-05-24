package com.netiapps.planetwork.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.netiapps.planetwork.dialogs.PlanetWorkDialogFragment;


public class AlertDialogShow {

    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    public static final int DEFAULT = 2;
    public static final int SIMPLE_ALERT = 1;
    public static final int YES_OR_NO_ALERT = 2;


    public static void showAlertDialog(String alertMessage, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(alertMessage);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    /**
     * Static method called to show an alert message to user
     *
     * @param alertMessage message to be shown
     * @param context context from respective screen
     */
    public static void showAlertDialogNew(String alertMessage, final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                showAlertDialogAfterClickedSave("Data Saved successfully",context);

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }



    /**
     * Static method called to show an alert message to user
     *
     * @param alertMessage message to be shown
     * @param context context from respective screen
     */
    public static void showAlertDialogAfterClickedSave(String alertMessage, final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    private Context context;
    private AlertDialogClickedListener alertDialogClickedListener;

    public static void showSimpleAlert(String title, String message, FragmentManager manager) {
        Bundle mBundle = new Bundle();
        mBundle.putString(Keys.ALERT_TITLE_KEY, title);
        mBundle.putString(Keys.ALERT_MESSAGE_KEY, message);
        mBundle.putInt(Keys.ALERT_TYPE_KEY, DEFAULT);
        mBundle.putInt(Keys.YES_OR_NO_OR_SIMPLE_ALERT_KEY, SIMPLE_ALERT);

        PlanetWorkDialogFragment dialogFragment = new PlanetWorkDialogFragment();
        dialogFragment.setArguments(mBundle);
        dialogFragment.show(manager, "PlanetWorkDialogFragment");


    }


    public interface AlertDialogClickedListener {
        public void okClicked(int id);
        public void cancelClicked(int id);
    }



    public AlertDialogShow(AlertDialogClickedListener listener, Context context) {

        this.context = context;
        alertDialogClickedListener = listener;
    }

    public void showOnlyPositiveOption(String alertMessage , final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialogClickedListener.okClicked(id);

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

    }

    public void showOnlyPositiveOptionWithOutCancelable(String alertMessage , final int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialogClickedListener.okClicked(id);

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        alertDialog.show();

    }


    public void showOnlyPositiveButtonAlert(String alertMessage,String positiveButtonText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();
    }


    public void showBothOptions(String alertMessage , final int id , String positiveInfo , String negativeInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton(positiveInfo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialogClickedListener.okClicked(id);

            }
        });

        builder.setNegativeButton(negativeInfo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialogClickedListener.cancelClicked(id);

            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

    }

    public void showBothOptionsWithDialogDismiss(String alertMessage , final int id , String positiveInfo , String negativeInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(alertMessage);
        builder.setPositiveButton(positiveInfo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                alertDialogClickedListener.okClicked(id);
            }
        });

        builder.setNegativeButton(negativeInfo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                alertDialogClickedListener.cancelClicked(id);
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

    }

}
