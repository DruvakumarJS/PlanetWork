package com.netiapps.planetwork.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.netiapps.planetwork.DashBoardActivity;
import com.netiapps.planetwork.MainActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.model.Customer;
import com.netiapps.planetwork.model.LatAndLong;
import com.netiapps.planetwork.model.Task;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class OverTimeFragment extends Fragment implements View.OnClickListener {

    private static final String CHANNEL_ID = "1001";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private List<Task> taskDeatils;
    private List<Customer> customerDeatisl;
    private AlertDialog mAlertDialogProductType;
    private AlertDialog mAlertCustomerType;
    private String taskID = "";
    private String barnchID = "";
    private String barnchName = "";
    private String taskTypeSelected = "";

    private String customerID = "";
    private String customerTypeSelected = "";
    private TextView tvtask;
    private TextView tvcustomer;
    private EditText editTextReason;
    private TextView tvUpdate;
    private RecyclerView recyclerView;

    private List<String> taskDetailsString;
    private List<String> customerdetailsString;
    private String operationModeSelected;
    private RadioButton radioButtonBusines;
    private RadioButton radioButtonIndiaval;
    private RadioGroup radioGroupseection;
    private static final String indivuald = "INDIVIDUAL";
    private static final String business = "BUSINESS";
    private String userId;
    private String time;
    private String formattedDate;
    private MyConsignmnetAdapter myConsignmnetAdapter;

    otStarted mCallback;
    private int notificationId=100;

    public OverTimeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.overtime, container, false);


        sharedPreferences = (SharedPreferences) getActivity().getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        userId = sharedPreferences.getString(Constants.userIdKey, "");


        taskDeatils = new ArrayList<>();
        taskDetailsString = new ArrayList<>();
        customerDeatisl = new ArrayList<>();
        customerdetailsString = new ArrayList<>();
        tvtask = (TextView) view.findViewById(R.id.tv_task);
        tvcustomer = (TextView) view.findViewById(R.id.tv_pening);
        radioButtonIndiaval = (RadioButton) view.findViewById(R.id.rdBusiness);
        radioButtonBusines = (RadioButton) view.findViewById(R.id.rdinduviadual);
        radioGroupseection = view.findViewById(R.id.radiogruop);
        editTextReason = view.findViewById(R.id.edt_reason);
        tvUpdate = view.findViewById(R.id.submitButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvcustomer.setOnClickListener(this);
        tvUpdate.setOnClickListener(this);


        //getThetaskFromServer();
        currentTime();
        currentDate();

       /* tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOT();
            }
        });*/


        tvtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskDetailsString.size() != 0) {

                    mAlertDialogProductType.show();

                } else {
                    // mAlertDialogShow.showOnlyPositiveOptionWithOutCancelable("No Task details found",CLOSE_THE_OPERATION_ALERT_ID);
                }
            }
        });

        tvcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (customerdetailsString.size() != 0) {

                    mAlertCustomerType.show();
                } else {
                    // mAlertDialogShow.showOnlyPositiveOptionWithOutCancelable("No Task details found",CLOSE_THE_OPERATION_ALERT_ID);
                }

            }
        });
        radioButtonIndiaval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                if (checked) {

                    taskDeatils.clear();
                    customerdetailsString.clear();
                    recyclerView.setAdapter(null);
                    tvcustomer.setText("Select Customer");
                    tvtask.setText("Select Task");
                    radioButtonIndiaval.setChecked(true);
                    operationModeSelected = "INDIVIDUAL";
                }
            }
        });

        radioButtonBusines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                if (checked) {

                    taskDeatils.clear();
                    customerdetailsString.clear();
                    recyclerView.setAdapter(null);
                    tvcustomer.setText("Select Customer");
                    tvtask.setText("Select Task");
                    radioButtonBusines.setChecked(true);
                    operationModeSelected = "BUSINESS";
                }

            }
        });

        radioGroupseection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                RadioButton mRadioButtonSelected = (RadioButton) group.findViewById(checkedId);
                String selected = mRadioButtonSelected.getText().toString().trim();
                if (selected.equalsIgnoreCase("INDIVIDUAL")) {
                    operationModeSelected = indivuald;
                    getTheCustomerDetailsFromServer(operationModeSelected);
                } else {
                    operationModeSelected = business;
                    getTheCustomerDetailsFromServer(operationModeSelected);
                }
                getThetaskFromServer();

            }
        });
      //  operationModeSelected = business;
       // getTheCustomerDetailsFromServer(operationModeSelected);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:

                break;

            case R.id.submitButton:
                if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1"))
                {
                    bottomsheet();
                    return;
                }
                else {
                    if (tvcustomer.getText().toString().trim().equalsIgnoreCase("Select Customer")) {
                        Toast.makeText(getActivity(), "Please select Customer ", Toast.LENGTH_SHORT).show();

                    }
                    else if (tvtask.getText().toString().trim().equalsIgnoreCase("Select Task")) {
                        Toast.makeText(getActivity(), "Please select Task ", Toast.LENGTH_SHORT).show();

                    }
                    else if (editTextReason.getText().toString().trim().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter the reason ", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        showOTconfirmationDialogue();
                    }

                }
                //show_task_notification();

                break;
        }

    }

    private void setUpOperationalModeIfUserRestarted() {

        String selectedBefore = getActivity().getIntent().getStringExtra(Constants.OPERATIONA_MODE_SELECT_BEFORE_KEY);
        if (selectedBefore == null) {

            radioButtonBusines.setChecked(true);

        } else {

            switch (selectedBefore) {

                case business:
                    radioButtonBusines.setChecked(true);
                    break;

                case indivuald:
                    radioButtonIndiaval.setChecked(true);
                    break;


                default:
                    radioButtonBusines.setChecked(true);
            }
        }

    }


    private void getThetaskFromServer() {

        Log.d(Constants.LOG, Constants.TASK);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.GET, Constants.TASK, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        parseTheJSONAndDisplayTheResults(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressDialog.dismiss();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                    return;
                }

                //AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };
        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);
    }

    private void parseTheJSONAndDisplayTheResults(JSONObject response) {

        try {

            taskDeatils.clear();
            taskDetailsString.clear();
            JSONArray JSONArray = response.getJSONArray("data");
            for (int i = 0; i < JSONArray.length(); i++) {

                JSONObject JSONObject = JSONArray.getJSONObject(i);
                Task task = new Task();
                task.setIiid(JSONObject.getString("id"));
                task.setName(JSONObject.getString("name"));

                taskDeatils.add(task);
                taskDetailsString.add(JSONObject.getString("name"));

            }
            setTheAdapterForProductType();

        } catch (Exception e) {

        }


    }

    private void setTheAdapterForProductType() {

        ListView mListView = new ListView(getActivity());
        ArrayAdapter arrayAdapterForProdcutCode = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                taskDetailsString);
        mListView.setAdapter(arrayAdapterForProdcutCode);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mAlertDialogProductType = mBuilder.create();
        mAlertDialogProductType.setView(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mAlertDialogProductType.dismiss();

                taskTypeSelected = taskDetailsString.get(position);
                Task prodcutDetailsForplans = taskDeatils.get(position);
                taskID = prodcutDetailsForplans.getIiid();

                tvtask.setText(taskTypeSelected + "  ");

            }
        });

    }

    private void getTheCustomerDetailsFromServer(String selectedmode) {


        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("customer_type", selectedmode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d(Constants.LOG, Constants.CUSTOMERS);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.CUSTOMERS,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        parseTheJSONAndTheResults(jsonArray);
                    } else if (status == 0) {
                        String message = "No Customer Found ";
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
                    Log.d("Data", error.toString());
                    Toast.makeText(getActivity(), "Please check your connectivity and try again", Toast.LENGTH_SHORT).show();
                    // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                    return;
                }
                Log.d("RESPONSE ERROR", error.toString());
                Toast.makeText(getActivity(), ErrorUtil.getTheErrorJSONObject(error), Toast.LENGTH_SHORT).show();

                // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);

    }

    private void showAlertOkAndClose(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //  dialogInterface.dismiss();
                dialogInterface.dismiss();
                /*final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, new HomeFragment(), "HomeFragmentTag");
                ft.commit();*/
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showAlertOkA(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
              /*  final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, new HomeFragment(), "HomeFragmentTag");
                ft.commit();*/
                startActivity(new Intent(getActivity(), DashBoardActivity.class));

            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }




    private void parseTheJSONAndTheResults(JSONArray jsonArray) {

        customerDeatisl.clear();
        customerdetailsString.clear();

        try {

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject JSONObject = jsonArray.getJSONObject(i);
                Customer customer = new Customer();
                customer.setIdd(JSONObject.getString("id"));
                customer.setCompany_name(JSONObject.getString("company_name"));
                customer.setCustomer_type(JSONObject.getString("customer_type"));
                customer.setFirst_name(JSONObject.getString("first_name"));
                customer.setLast_name(JSONObject.getString("last_name"));

                JSONArray jsonArray1 = JSONObject.getJSONArray("customer_location");

                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                    customer.setCustomer_id(jsonObject.getString("customer_id"));
                    customer.setBranch(jsonObject.getString("branch"));
                    customer.setAddress(jsonObject.getString("address"));
                }


                customerDeatisl.add(customer);
                if (JSONObject.getString("customer_type").equals(indivuald)) {
                    customerdetailsString.add(JSONObject.getString("first_name") + JSONObject.getString("last_name"));
                } else {
                    customerdetailsString.add(JSONObject.getString("company_name"));

                }

            }
            setTheAdapterForCustomerType();




        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void setTheAdapterForCustomerType() {

        ListView mListView = new ListView(getActivity());
        ArrayAdapter arrayAdapterForProdcutCode = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                customerdetailsString);
        mListView.setAdapter(arrayAdapterForProdcutCode);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mAlertCustomerType = mBuilder.create();
        mAlertCustomerType.setView(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mAlertCustomerType.dismiss();

                customerTypeSelected = customerdetailsString.get(position);
                Customer prodcutDetailsForplans = customerDeatisl.get(position);
                customerID = prodcutDetailsForplans.getIdd();

                tvcustomer.setText(customerTypeSelected + "  ");

               // bottomsheet();

                if (customerDeatisl.size() > 0) {
                    myConsignmnetAdapter = new MyConsignmnetAdapter(getActivity(), customerDeatisl);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(myConsignmnetAdapter);


                } else {
                    Toast.makeText(getActivity(), "NO Branch", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public class MyConsignmnetAdapter extends RecyclerView.Adapter<MyConsignmnetAdapter.ViewHolder> {

        private List<Customer> itemList = new ArrayList<>();
        private Context context;
        private int lastSelectedPosition = -1;

        public MyConsignmnetAdapter(Context context, List<Customer> items) {
            if (items != null && !items.isEmpty()) {
                itemList = items;
            }
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.barnchselcter, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Customer customer = itemList.get(position);


            holder.tvbranch.setText(customer.getBranch());

            holder.tvcitybranch.setText(customer.getBranch());

            holder.tvaddress.setText("Address : " + customer.getAddress());
            
            holder.setIsRecyclable(false);

            holder.rdradiochecked.setChecked(lastSelectedPosition == position);



            holder.rdradiochecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastSelectedPosition = holder.getAdapterPosition();
                    barnchID=customer.getIdd();
                    barnchName=customer.getBranch();

                    Log.v("TAG","Customer id "+barnchID);
                    Log.v("TAG","barnchName "+barnchName);
                    notifyDataSetChanged();
                }
            });

            holder.cardBranchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.rdradiochecked.setChecked(true);
                    lastSelectedPosition = holder.getAdapterPosition();

                    barnchID=customer.getIdd();
                    barnchName=customer.getBranch();

                    Log.v("TAG","Customer id "+barnchID);
                    Log.v("TAG","barnchName "+barnchName);


                    notifyDataSetChanged();
                }
            });



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

            TextView tvbranch;
            TextView tvcitybranch;
            TextView tvaddress;
            RadioButton rdradiochecked;
            RadioGroup radiogruop;
            View itemView;
            CardView cardBranchView;


            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tvbranch = (TextView) itemView.findViewById(R.id.tvsbranch);
                tvcitybranch = (TextView) itemView.findViewById(R.id.tvcitybranch);
                tvaddress = (TextView) itemView.findViewById(R.id.tvaddress);
                rdradiochecked = (RadioButton) itemView.findViewById(R.id.rdradio);
                radiogruop = (RadioGroup) itemView.findViewById(R.id.radiogruop);
                cardBranchView=itemView.findViewById(R.id.card_branch);

            }
        }
    }


    private void currentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        time = date.format(currentLocalTime);
    }

    private void currentDate() {
//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//         formattedDate = df.format(c);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        formattedDate = sdf.format(calendar.getTime());
    }


    private void sendTheOvertimeSendDetailsToServer() {

        String date = formattedDate;
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("customer_id", customerID);
            jsonObject.put("task_id", taskID);
            jsonObject.put("branch_id",barnchID);
            jsonObject.put("date", date);
            jsonObject.put("start_time", time);
            jsonObject.put("end_time", time);
            jsonObject.put("description", editTextReason.getText().toString().trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d(Constants.LOG, Constants.OVERTIME);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.OVERTIME,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                       /* String message = "Overtime Created Successfully ";
                        showAlertOkA(message);*/
                        startOT();
                    } else if (status == 0) {
                        String message = "Overtime Not Created ";
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
                    Log.d("Data", error.toString());
                    Toast.makeText(getActivity(), "Please check your connectivity and try again", Toast.LENGTH_SHORT).show();
                    // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                    return;
                }
                Log.d("RESPONSE ERROR", error.toString());
                Toast.makeText(getActivity(), ErrorUtil.getTheErrorJSONObject(error), Toast.LENGTH_SHORT).show();

                // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);

    }

    private void show_alert() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("PlanetWork")
                .setMessage("NOTE : You cannot a create a job during worikng hours")
                .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                /*  .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {

                      }
                  })*/
                .show();
    }



    private void sendTheLatAndLongDetailsToServer() {


        final JSONArray serviceJSONArray;
        try {
            serviceJSONArray = new JSONArray("data");
            List<LatAndLong> mServiceList = new ArrayList<>();

            for (int i = 0; i < serviceJSONArray.length(); i++) {
                JSONObject serviceJSONObject = serviceJSONArray.getJSONObject(i);
                LatAndLong latAndLong = new LatAndLong();
                latAndLong.setUser_id(serviceJSONObject.getString("user_id"));
                latAndLong.setUser_id(serviceJSONObject.getString("date"));
                latAndLong.setUser_id(serviceJSONObject.getString("time"));
                latAndLong.setUser_id(serviceJSONObject.getString("lat"));
                latAndLong.setUser_id(serviceJSONObject.getString("lng"));
                latAndLong.setUser_id(serviceJSONObject.getString("status"));

                mServiceList.add(latAndLong);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d(Constants.LOG, Constants.LOCATIONINSERT);
        JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOCATIONINSERT,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(Constants.LOG, response.toString());
                progressDialog.dismiss();
                try {
                    int status = response.getInt("status");
                    if (status == 1) {
                        String message = "Overtime Created Successfully ";
                        showAlertOkA(message);
                    } else if (status == 0) {
                        String message = "Overtime Not Created ";
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
                    Log.d("Data", error.toString());
                    Toast.makeText(getActivity(), "Please check your connectivity and try again", Toast.LENGTH_SHORT).show();
                    // AlertDialogShow.showSimpleAlert("No Connectivity", "Please check your connectivity and try again", getSupportFragmentManager());
                    return;
                }
                Log.d("RESPONSE ERROR", error.toString());
                Toast.makeText(getActivity(), ErrorUtil.getTheErrorJSONObject(error), Toast.LENGTH_SHORT).show();

                // AlertDialogShow.showSimpleAlert("Failed", ErrorUtil.getTheErrorJSONObject(error), getSupportFragmentManager());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("Accept", "application/json");
                mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                return mHeaders;
            }

        };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                30 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        PlanetWorkVolleySingleton.getInstance(getActivity()).addToRequestQueue(mRequest);

    }

    public void bottomsheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        bottomSheetDialog.setCancelable(false);

        TextView tvcaution=bottomSheetDialog.findViewById(R.id.tvcaustionview);
        Button btngotit=bottomSheetDialog.findViewById(R.id.btngot);

        tvcaution.setText("Caution : You cannot create your own task during working hours");
        btngotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.show();

    }

    private void showOTconfirmationDialogue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You are creating a self task @"+ barnchName+" branch.\nWould you like to proceed");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                sendTheOvertimeSendDetailsToServer();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (otStarted) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

    public interface otStarted {
        public void starttimer(String text);
    }
    public void startOT(){
        taskDeatils.clear();
        customerdetailsString.clear();
        recyclerView.setAdapter(null);
        tvcustomer.setText("Select Customer");
        tvtask.setText("Select Task");
        editTextReason.setHint("Please Write a Note");


        mEditor.putString("ot_started","true");
        mEditor.apply();

        mCallback.starttimer("start_timer_for_ot");

        String curent_time = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

        String tittle="PlanetWork Self task Started";
        String message="Your self task has been started on "+curent_time;
        //show_task_notification();
        LocalHelper.showNotification(getActivity(),tittle,message);
    }

    private void show_task_notification() {
        String curent_time = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date());

        String message="Your task has been started on "+curent_time;

        Intent intent = new Intent(getActivity(), DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getActivity(), channelId)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle("PlanetWork task Started")
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "PlanetWork",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Random r = new Random();
        int randomNumber = r.nextInt(10);
        notificationManager.notify(randomNumber /* ID of notification */, notificationBuilder.build());


    }


}
