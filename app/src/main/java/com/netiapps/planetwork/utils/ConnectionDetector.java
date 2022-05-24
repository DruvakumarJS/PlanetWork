package com.netiapps.planetwork.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Used to check connection of Mobile/Application with Internet data connection , GPS etc...
 *
 * @author Harish
 * @author Rajesh
 * @author Suman
 *
 */
public class ConnectionDetector {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public ConnectionDetector(Context mContext) {

        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(Constants.sharedPreferencesKey, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    public boolean isConnectingToInternet() {

        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {

            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();

            if (activeNetwork != null) {

                return true;

            }
        }

        return false;
    }
}

