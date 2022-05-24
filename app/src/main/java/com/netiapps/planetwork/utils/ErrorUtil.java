package com.netiapps.planetwork.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorUtil {

    public static String getTheErrorJSONObject(VolleyError error) {

        NetworkResponse networkResponse = error.networkResponse;
        if(networkResponse == null) {
            return "OOPs something gone wrong in the server. Please try again later";
        }

        String errorString = new String(networkResponse.data);
        try {
            JSONObject jsonObject = new JSONObject(errorString);
            return jsonObject.getString("error");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(errorString);
            return jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "OOPs something gone wrong in the server. Please try again later";

    }
}
