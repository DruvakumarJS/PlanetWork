package com.netiapps.planetwork;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CircularProgressButton butoonLogin;
    private LinearLayout llLogin;
    private LinearLayout llOtp;
    private CircularProgressButton buttonvalidateOtp;
    private EditText edtuserMobile;
    private Constants mConstants;
    private String androidId;
    private String edtMobile;
    private EditText edt1;
    private EditText edt2;
    private EditText edt3;
    private EditText edt4;
    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;
    private String edet1;
    private String edet2;
    private String edet3;
    private String edet4;
    String otp;
    private CircularProgressButton ciValidateOtp;
    private TextView tvResendOtp;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llLogin = findViewById(R.id.lllogin);
        llOtp = findViewById(R.id.llotp);
        edtuserMobile = findViewById(R.id.editText_login_userName);
        edt1 = findViewById(R.id.et_digit1);
        edt2 = findViewById(R.id.et_digit2);
        edt3 = findViewById(R.id.et_digit3);
        edt4 = findViewById(R.id.et_digit4);
        ciValidateOtp = findViewById(R.id.crLoginButton);
        tvResendOtp = findViewById(R.id.tv_resendOTP);


        edt1.addTextChangedListener(new GenericTextWatcher(edt1));
        edt2.addTextChangedListener(new GenericTextWatcher(edt2));
        edt3.addTextChangedListener(new GenericTextWatcher(edt3));
        edt4.addTextChangedListener(new GenericTextWatcher(edt4));

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.rel));

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();


        androidId=android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Log.v("TAG", "deviceID "+androidId);

        // getImei();
       /* if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                androidId = null;
            } else {
                androidId = extras.getString("IMEINumber");
            }
        } else {
            androidId = (String) savedInstanceState.getSerializable("IMEINumber");
        }
*/

        butoonLogin = findViewById(R.id.cirLoginButton);
        butoonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTheLogin();
//                llOtp.setVisibility(View.VISIBLE);
//                llLogin.setVisibility(View.GONE);


            }
        });


        ciValidateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTheOTP();
//                Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
//                startActivity(i);
            }
        });
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTheReset();
            }
        });

       /* edt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                edt1.setFocusable(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt1.getEditableText().toString().length()==1)

                {
                    edt2.setFocusable(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                edt1.setFocusable(false);
                edt2.setFocusable(true);
            }
        });

        edt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                edt2.setFocusable(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt2.getEditableText().toString().length()==1)

                {
                    edt3.setFocusable(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                edt2.setFocusable(false);
                edt3.setFocusable(true);
            }
        });

        edt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                edt3.setFocusable(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt3.getEditableText().toString().length()==1)

                {
                    edt4.setFocusable(true);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                edt3.setFocusable(false);
                edt4.setFocusable(true);
            }
        });

        edt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                edt4.setFocusable(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edt4.getEditableText().toString().length()==1)

                {
                   // edt4.setFocusable(false);
                    ciValidateOtp.setFocusable(true);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt1.setFocusable(true);
                edt1.setCursorVisible(true);
                ciValidateOtp.setFocusable(true);
            }
        });*/


        startSmartUserConsent();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.editText_login_userName:

                break;
        }
    }

    private void validateTheLogin() {

        edtMobile = edtuserMobile.getText().toString().trim();

        if (edtMobile.isEmpty()) {
            AlertDialogShow.showAlertDialog("Please complete the inputs", this);
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edtMobile);
             jsonObject.put("imei", androidId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d(Constants.LOG, jsonObject.toString());
        Log.d(Constants.LOG, mConstants.LOGIN_API_URL);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, mConstants.LOGIN_API_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.LOG, response.toString());
                        progressDialog.dismiss();

                        try {
                            if (response.getInt("status") == 1) {
                                llOtp.setVisibility(View.VISIBLE);
                                llLogin.setVisibility(View.GONE);
                            } else if (response.getInt("status") == 0) {
                                String message = response.getString("message");
                                showAlertDialogOkAndClose(message);
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
                    AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());


                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                        AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", getSupportFragmentManager());

                        return;

                    }
                }
                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }
        };


        PlanetWorkVolleySingleton.getInstance(this).addToRequestQueue(mRequest);

    }

    private void showAlertOkAndClose(String alertMessage) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //  finish();
                edt1.setText("");
                edt2.setText("");
                edt3.setText("");
                edt4.setText("");
                edt1.requestFocus();



            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showAlertDialogOkAndClose(String alertMessage) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //  finish();
                startActivity(getIntent());
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT) {

            if ((resultCode == RESULT_OK) && (data != null)) {

                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);


            }


        }

    }

    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{4}");
        if (message != null) {
            Matcher mMatcher = otpPattern.matcher(message);
            if (mMatcher.find()) {
                otp = mMatcher.group(0);
                edt1.setText(otp.substring(0, 1));
                edt2.setText(otp.substring(1, 2));
                edt3.setText(otp.substring(2, 3));
                edt4.setText(otp.substring(3, 4));

            } else {
                AlertDialogShow.showAlertDialog("Failed to extract the OTP!!", this);


            }
        }


    }

    private void registerBroadcastReceiver() {

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent, REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void validateTheOTP() {

        otp = edt1.getText().toString().trim() + edt2.getText().toString().trim() + edt3.getText().toString().trim()
                + edt4.getText().toString().trim();
        if (otp.isEmpty()) {
            AlertDialogShow.showAlertDialog("Please complete the fields", this);
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edtMobile);
            jsonObject.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d(Constants.LOG, jsonObject.toString());
        Log.d(Constants.LOG, mConstants.VERIFY_OTP);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, mConstants.VERIFY_OTP, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.LOG, response.toString());
                        progressDialog.dismiss();

                        try {
//
                            int status = response.getInt("status");
                            if (status == 1) {
                                parseTheResponseAndSave(response);

                            } else if (response.getInt("status") == 0) {
                                String message = response.getString("message");
                                showAlertOkAndClose(message);
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
                    AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());


                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                        AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", getSupportFragmentManager());

                        return;

                    }
                }
                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }
        };


        PlanetWorkVolleySingleton.getInstance(this).addToRequestQueue(mRequest);

    }

    private void parseTheResponseAndSave(JSONObject fullJSONObject) {

        try {

            mEditor.putString(Keys.ACCESS_TOKEN_KEY, fullJSONObject.getString("access_token"));

            JSONObject infoJsonObject = fullJSONObject.getJSONObject("user");

            mEditor.putString(Constants.userIdKey, infoJsonObject.getString("id"));
            mEditor.putString(Constants.userNameKey, infoJsonObject.getString("name"));
            mEditor.putString(Constants.email, infoJsonObject.getString("email"));
            mEditor.putString(Constants.mobileNumber, infoJsonObject.getString("mobile"));
            mEditor.putString(Constants.photo,infoJsonObject.getString("profile_photo_url"));

            mEditor.putBoolean(Constants.loginStatusKey,true);
            mEditor.apply();
            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
            finish();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void validateTheReset() {

        edt1.setText("");
        edt2.setText("");
        edt3.setText("");
        edt4.setText("");

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", edtMobile);
              jsonObject.put("imei", androidId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d(Constants.LOG, jsonObject.toString());
        Log.d(Constants.LOG, mConstants.LOGIN_API_URL);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, mConstants.LOGIN_API_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.LOG, response.toString());
                        progressDialog.dismiss();

                        try {
                            if (response.getInt("status") == 1) {
                                startSmartUserConsent();
                            } else if (response.getInt("status") == 0) {
                                String message = response.getString("message");
                                showAlertDialogOkAndClose(message);
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
                    AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());

                    return;
                }
                NetworkResponse networkResponse = error.networkResponse;
                Log.d("RESPONSE ERROR", error.toString());


                if (networkResponse != null) {
                    if (networkResponse.statusCode == 401) {
                        AlertDialogShow.showSimpleAlert("Failed", "Invalid Credentials", getSupportFragmentManager());

                        return;

                    }
                }
                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");

                return mHeaders;
            }
        };


        PlanetWorkVolleySingleton.getInstance(this).addToRequestQueue(mRequest);

    }

    @Override
    public void onBackPressed() {
        llOtp.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        edt1.setText("");
        edt2.setText("");
        edt3.setText("");
        edt4.setText("");


    }

    private void functionCheckLogin() {

        Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
        mEditor.putBoolean(Constants.loginStatusKey, true);
        mEditor.commit();
        startActivity(i);
        finish();


    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;
        public GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.et_digit1:
                    if(text.length()==1)
                        edt2.requestFocus();
                    break;
                case R.id.et_digit2:
                    if(text.length()==1)
                        edt3.requestFocus();
                    else if(text.length()==0)
                        edt1.requestFocus();
                    break;
                case R.id.et_digit3:
                    if(text.length()==1)
                        edt4.requestFocus();
                    else if(text.length()==0)
                        edt2.requestFocus();
                    break;
                case R.id.et_digit4:
                    if(text.length()==1)
                      ciValidateOtp.requestFocus();
                    else if(text.length()==0)
                        edt3.requestFocus();
                    break;
            }

        }
    }
}
