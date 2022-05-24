package com.netiapps.planetwork;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.netiapps.planetwork.model.Leave;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaveActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private CircularProgressButton leaveButton;
    private TextView tvFrDate;
    private EditText ediReason;
    private TextView tvToDate;
    private Dialog mDialogHeader;
    private TextView tvPrelivLeave;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userId;
    private String operationModeSelected;
    private static final String medicalLeave = "ML";
    private static final String previlageLeave = "CL";
    private RecyclerView recyclerView;
    private List<Leave> myLeaveListt;
    private MyConsignmnetAdapter myConsignmnetAdapter;
    private List<TextView> listLeaveText;
    private List<TextView> listststusText;
    private List<TextView> listfromText;
    private List<TextView> listtoText;
    private List<TextView> listresonNumber;
    private List<TextView> listnoofdaysNumber;
    private SimpleDateFormat dateFormatter;

    private boolean isToDateClicked = false;
    private ImageView imgBcak;
    Dialog mDialogg;
    CardView cardimgBcak;
    TextView tvheader;
    CircleImageView cardivprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave);

        mSharedPreferences = getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        userId = mSharedPreferences.getString(Constants.userIdKey, "");

        leaveButton = findViewById(R.id.cirLeaveButton);
        tvPrelivLeave = findViewById(R.id.tvPelLeave);
        imgBcak = findViewById(R.id.iv_back);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        cardimgBcak = findViewById(R.id.cardimage);
        tvheader=findViewById(R.id.tvheader);
        cardivprofile=findViewById(R.id.ivcircleimg);

        Picasso.get().load(R.drawable.ic_person).into(cardivprofile);


        tvheader.setText("My Leaves");
        cardivprofile.setVisibility(View.VISIBLE);
        myLeaveListt = new ArrayList<>();

        listLeaveText = new ArrayList<TextView>();
        listststusText = new ArrayList<TextView>();
        listfromText = new ArrayList<TextView>();
        listtoText = new ArrayList<TextView>();
        listresonNumber = new ArrayList<TextView>();
        listnoofdaysNumber = new ArrayList<TextView>();


        leaveButton.setOnClickListener(this);
        //imgBcak.setOnClickListener(this);

       // getTheLeaveDetails();
         cardivprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cardimgBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTheProfileFromServer();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cirLeaveButton:
                customDialg();
                break;

            case R.id.cardimage:
             finish();
                break;
        }

    }

    public void customDialg() {
        final Dialog mDialog = new Dialog(LeaveActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.alertdialogleave);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final TextView tvFromdate = (TextView) mDialog.findViewById(R.id.tvfromdate);
        final TextView tvTodate = (TextView) mDialog.findViewById(R.id.tv_toDate);
        final RadioButton rdPendingLeave = (RadioButton) mDialog.findViewById(R.id.rdPening);
        final RadioButton rdMedicalLeave = (RadioButton) mDialog.findViewById(R.id.rdMedicalLeave);
        final EditText edtReason = (EditText) mDialog.findViewById(R.id.edt_reason);
        final RadioGroup mRadioGroupSelection = (RadioGroup) mDialog.findViewById(R.id.radiogruop);
        final TextView tvupdate = (TextView) mDialog.findViewById(R.id.submitButton);
        final TextView tvCancel = (TextView) mDialog.findViewById(R.id.TvCancel);


        tvFrDate = tvFromdate;
        tvToDate = tvTodate;
        ediReason = edtReason;
        mDialogHeader = mDialog;


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        tvupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getApplyLeave();
            }
        });
        tvFromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                isToDateClicked = false;
                showDatePickerDialog();
            }
        });

        tvTodate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                isToDateClicked = true;
                showDatePickerDialog();
            }
        });

        edtReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        rdMedicalLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                if (checked) {
                    rdMedicalLeave.setChecked(true);
                    operationModeSelected = "ML";
                }
            }
        });

        rdPendingLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                if (checked) {
                    rdMedicalLeave.setChecked(true);
                    operationModeSelected = "CL";
                }

            }
        });

        mRadioGroupSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                RadioButton mRadioButtonSelected = (RadioButton) group.findViewById(checkedId);
                String selected = mRadioButtonSelected.getText().toString();
                if (!selected.equalsIgnoreCase("Privilege Leaves")) {
                    operationModeSelected = medicalLeave;
                } else {
                    if ((selected.equalsIgnoreCase("CL"))) {
                        operationModeSelected = previlageLeave;
                    }
                }

            }
        });
        operationModeSelected = previlageLeave;

        mDialogg = mDialog;
        mDialog.show();
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.DialogTheme,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (month >= 0) {
            month = month + 1;
        }
        String date = dayOfMonth + "/" + month + "/" + year;
        if (tvFrDate != null) {
            if (isToDateClicked) {
                tvToDate.setText(date);
                tvToDate.setTag(R.string.to_date,year+"/"+month+"/"+dayOfMonth);
            } else {
                tvFrDate.setText(date);
                tvFrDate.setTag(R.string.from_date,year+"/"+month+"/"+dayOfMonth);
            }
        }

        if (mDialogHeader != null) {
            mDialogHeader.show();
        }

    }

    private void getApplyLeave() {
        String Textviewfromdate = (String) tvFrDate.getTag(R.string.from_date); //tvFrDate.getText().toString().trim();
        String TextviewToDate = (String) tvToDate.getTag(R.string.to_date); //tvToDate.getText().toString().trim();
        String editres = ediReason.getText().toString().trim();

        if (Textviewfromdate.isEmpty()) {
            AlertDialogShow.showSimpleAlert("", "From Date cannot be empty", getSupportFragmentManager());
            return;

        }
        if (TextviewToDate.isEmpty()) {
            AlertDialogShow.showSimpleAlert("", "To Date Cannot be empty", getSupportFragmentManager());
            return;
        }
        if (editres.isEmpty()) {
            AlertDialogShow.showSimpleAlert("", "Please write the reason for leave..", getSupportFragmentManager());
            return;
        }


        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("leave_type",operationModeSelected);
            jsonObject.put("from_date", Textviewfromdate);
            jsonObject.put("to_date", TextviewToDate);
            jsonObject.put("reason", ediReason.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(Constants.LOG, jsonObject.toString());
        final ProgressDialog progressDialog = new ProgressDialog(LeaveActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d(Constants.LOG, Constants.APPLY_LEAVE);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.APPLY_LEAVE,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        String message = response.getString("message");
                        showAlertOkAndClose(message);
                    } else {

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
                Log.d("RESPONSE ERROR", error.toString());

                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };

        PlanetWorkVolleySingleton.getInstance(LeaveActivity.this).addToRequestQueue(mRequest);

    }

    private void showAlertOkAndClose(String alertMessage) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();
               if(mDialogg !=null){
                   mDialogg.dismiss();
               }
               finish();

            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void getTheLeaveDetails() {


        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(Constants.LOG, jsonObject.toString());
        final ProgressDialog progressDialog = new ProgressDialog(LeaveActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.d(Constants.LOG, Constants.LEAVECOUNT);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LEAVECOUNT,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        JSONObject jsonObject1 = response.getJSONObject("leaves");
                        tvPrelivLeave.setText(jsonObject1.getString("available_cl"));
                    } else {

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
                Log.d("RESPONSE ERROR", error.toString());

                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };

        PlanetWorkVolleySingleton.getInstance(LeaveActivity.this).addToRequestQueue(mRequest);

    }

    private void getTheProfileFromServer() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d(Constants.LOG, Constants.LEAVE_HISTORY);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LEAVE_HISTORY,
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
                        String message = "No Leave Found";

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
                Log.d("RESPONSE ERROR", error.toString());

                AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + mSharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(LeaveActivity.this).addToRequestQueue(mRequest);

    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {

        try {

            JSONObject jsonObject1 = response.getJSONObject("leaves");
            tvPrelivLeave.setText(jsonObject1.getString("earned_leave"));

            JSONArray jsonArray = response.getJSONArray("history");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject categoryJSONObject = jsonArray.getJSONObject(i);

                Leave leave = new Leave();
                leave.setFrom_date(categoryJSONObject.getString("from_date"));
                leave.setTo_date(categoryJSONObject.getString("to_date"));
                leave.setLeave_type(categoryJSONObject.getString("leave_type"));
                leave.setReason(categoryJSONObject.getString("reason"));
                leave.setStatus(categoryJSONObject.getString("status"));
                leave.setNo_of_days(categoryJSONObject.getString("no_of_days"));

                myLeaveListt.add(leave);

            }


        } catch (Exception e) {

        }

        if (myLeaveListt.size() > 0) {
            myConsignmnetAdapter = new MyConsignmnetAdapter(LeaveActivity.this, myLeaveListt);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(myConsignmnetAdapter);


        } else {
            Toast.makeText(LeaveActivity.this, "NO Leave", Toast.LENGTH_SHORT).show();
        }

    }


    public class MyConsignmnetAdapter extends RecyclerView.Adapter<MyConsignmnetAdapter.ViewHolder> {

        private List<Leave> inputList = new ArrayList<>();
        private List<Leave> itemList = new ArrayList<>();
        private Context context;

        public MyConsignmnetAdapter(Context context, List<Leave> items) {
            if (items != null && !items.isEmpty()) {
                itemList = items;
                inputList = items;
            }
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.leave_list, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Leave leavedata = itemList.get(position);

            holder.tvTitle.setText(leavedata.getFrom_date() + "  -  " + leavedata.getTo_date());

            holder.tvTitlecompnay.setText(leavedata.getReason());

            holder.tvitldayscompnay.setText(leavedata.getNo_of_days());

            if (leavedata.getLeave_type().contains("CL")) {
                holder.tvNameDate.setText("Privilege Leaves");
            } else if (leavedata.getLeave_type().contains("ML")) {
                holder.tvNameDate.setText("Medical Leaves");
            }

            switch (leavedata.getStatus()){
                case "pending" :
                    holder.tvstatustask.setText("Pending");
                    holder.tvstatustask.setBackgroundResource(R.drawable.border_button);
                    holder.tvstatustask.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.yellowlihh)));
                    break;
                case "rejected"  :
                    holder.tvstatustask.setText("Rejected");
                    holder.tvstatustask.setBackgroundResource(R.drawable.complerejected);
                    holder.tvstatustask.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.recolor)));
                   // holder.tvstatustask.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.rejectred)));
                    break;
                case "approved" :
                    holder.tvstatustask.setText("Approved");
                    holder.tvstatustask.setBackgroundResource(R.drawable.completed_back);
                    holder.tvstatustask.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.approvegrrenn)));
                    break;
                case "modify-approved" :
                    holder.tvstatustask.setText("Modify/Approved");
                    holder.tvstatustask.setBackgroundResource(R.drawable.completed_back);
                    holder.tvstatustask.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.approvegrrenn)));
                    break;
            }
//            if (leavedata.getStatus().equalsIgnoreCase("pending")) {
//                holder.tvstatustask.setText("Pending");
//                holder.tvstatustask.setBackgroundResource(R.drawable.border_button);
//                holder.tvstatustask.setTextColor(R.color.pedingyelow);
//            } else if (leavedata.getStatus().equalsIgnoreCase("rejected")) {
//                holder.tvstatustask.setText("Rejected");
//                holder.tvstatustask.setBackgroundResource(R.drawable.complerejected);
//                holder.tvstatustask.setTextColor(R.color.rejectred);
//            } else if (leavedata.getStatus().equalsIgnoreCase("approved")) {
//                holder.tvstatustask.setText("Approved");
//                holder.tvstatustask.setBackgroundResource(R.drawable.completed_back);
//                holder.tvstatustask.setTextColor(R.color.approvegrrenn);
//            }

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

            TextView tvNameDate;
            TextView tvstatustask;
            TextView tvTitlecompnay;
            TextView tvTitle;
            TextView tvitldayscompnay;
            View itemView;


            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvNameDate = (TextView) itemView.findViewById(R.id.tvNameDate);
                tvTitlecompnay = (TextView) itemView.findViewById(R.id.tvTitlecompnay);
                tvstatustask = (TextView) itemView.findViewById(R.id.tvstatustask);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvitldayscompnay = (TextView) itemView.findViewById(R.id.tvitldayscompnay);

            }
        }
    }


}
