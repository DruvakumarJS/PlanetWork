package com.netiapps.planetwork;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ncorti.slidetoact.SlideToActView;
import com.netiapps.planetwork.adapter.AdapterTaskImages;
import com.netiapps.planetwork.geofencing.GeofencingService;
import com.netiapps.planetwork.model.ApiModel;
import com.netiapps.planetwork.model.TaskImagesDAO;
import com.netiapps.planetwork.network_retrofit.RetrofitClient;
import com.netiapps.planetwork.retrofit.ApiConstants;
import com.netiapps.planetwork.retrofit.ServiceInterface;
import com.netiapps.planetwork.utils.AlertDialogShow;
import com.netiapps.planetwork.utils.Constants;
import com.netiapps.planetwork.utils.ErrorUtil;
import com.netiapps.planetwork.utils.FileUtil;
import com.netiapps.planetwork.utils.Keys;
import com.netiapps.planetwork.utils.LocalHelper;
import com.netiapps.planetwork.utils.PlanetWorkVolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class TaskDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView tvpening;
    private String selectedStatusCode;
    private TextView tvPeingDate;
    private ImageView imgMap;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ImageView img1;
    private ImageView img2;
    ImageView addImage;
    String newString, newTask, newtaskStatus, srnumber, pos, newlat, newlong;
    String newCompany;
    String newbrnach;
    String numberVisit;
    String jobassignId;
    String jobId, images;
    String edtDescriptioncontent;
    private EditText edtDescription;
    private TextView tvdate;
    private TextView tvtask;
    private TextView tvBranch;
    private TextView tvNoofVisit;
    private TextView tvsrnumber;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static String fileName = null;
    private String filename = "";
    RequestBody postBodyImage;
    private File takenPicture;
    private static final int REQUEST_IMAGE_CAPTURE_CAMERA = 1;
    int count = 0;
    LinearLayout llimagelist;

    double latitude;
    double longitude;
    private GpsTracker gpsTracker;
    private TextView tvupdate;
    String userId;
    private ImageView imgback;
    private TextView tvheader;
    private LinearLayout linearLayout;
    ImageView selectedImage;
    ;
    private LinearLayout parentLinearLayout;
    List<Uri> files = new ArrayList<>();
    CircularProgressButton btnSubmit;
    ServiceInterface serviceInterface;
    LinearLayout lldummyimageview;
    int no_of_visits;
    boolean isupdated = false;

    String St_UesrId, St_jobId, St_task_status, St_description, updatedStatus;
    RecyclerView rvImagelist;

    ArrayList<String> imagelist = new ArrayList<>();
    List<List<String>> test = new ArrayList<>();

    AdapterTaskImages adapterTaskImages;
    JSONObject jsonObject = new JSONObject();

    ArrayList<TaskImagesDAO> taskimagelist = new ArrayList<>();
    ImageView start, dest, work, hold, done;
    View v1, v2, v3, v4;
    SlideToActView sliderStart, sliderStop;
    String taskImages="[\n" +
            "  {\n" +
            "    \"img\": \"https://st.depositphotos.com/2245963/2392/i/600/depositphotos_23929049-stock-photo-android-robot.jpg\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"img\": \"https://9to5google.com/wp-content/uploads/sites/4/2021/10/One-UI-4.0-Android-12-4.jpg?quality=82&strip=all&w=1600\"\n" +
            "  }\n" +
            "]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusactivyty);

        sharedPreferences = (SharedPreferences) getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();

        userId = sharedPreferences.getString(Constants.userIdKey, "");


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/" + System.currentTimeMillis() + ".jpg";
        checkPermission();

        tvpening = findViewById(R.id.tv_pening);
        imgMap = findViewById(R.id.img_map);
        img1 = findViewById(R.id.pic1);
        img2 = findViewById(R.id.pic2);
        tvdate = findViewById(R.id.tvdate);
        imgback = findViewById(R.id.ivback);
        tvtask = findViewById(R.id.tvtask);
        tvBranch = findViewById(R.id.tvbrnahc);
        tvNoofVisit = findViewById(R.id.tvitlcompnay);
        edtDescription = findViewById(R.id.edtdescrption);
        tvupdate = findViewById(R.id.tv_update);
        parentLinearLayout = findViewById(R.id.parent_linear_layout);
        lldummyimageview = findViewById(R.id.lldummyimageview);
        tvheader = findViewById(R.id.tvheader);
        tvsrnumber = findViewById(R.id.tvsrnumber);
        rvImagelist = findViewById(R.id.rvimagelist);
        llimagelist = findViewById(R.id.llimagelist);
        sliderStart = findViewById(R.id.slider_start);
        sliderStop = findViewById(R.id.slider_stop);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
                newCompany = null;
                newbrnach = null;
                numberVisit = null;
                jobassignId = null;
                jobId = null;
                newTask = null;
                newtaskStatus = null;
                srnumber = null;
                //  pos=null;
                images = null;
                newlat = null;
                newlong = null;
            } else {
                newString = extras.getString("Date");
                newCompany = extras.getString("Copany");
                newbrnach = extras.getString("Branch");
                numberVisit = extras.getString("NoOfVisit");
                jobassignId = extras.getString("JobAssignId");
                jobId = extras.getString("JobId");
                newTask = extras.getString("task");
                newtaskStatus = extras.getString("task_status");
                srnumber = extras.getString("sr_no");
                //pos=extras.getString("position");
                newlat = extras.getString("lat");
                newlong = extras.getString("long");
                //  images=extras.getString("images");
                // images = extras.getString("images");
            }

        } else {
            newString = (String) savedInstanceState.getSerializable("Date");
        }

        Log.v("TAG", "imagelist" + images);
        no_of_visits = Integer.parseInt(numberVisit);

        getImagelist(taskImages);

        tvdate.setText(newString);
        tvtask.setText(newTask);
        tvBranch.setText(newbrnach);
        tvNoofVisit.setText(numberVisit);
        tvpening.setText(newtaskStatus);
        tvsrnumber.setText("SR NO : " + srnumber);

        if (newtaskStatus.equalsIgnoreCase("inProgress") && !sharedPreferences.getString("job_id", "0").equalsIgnoreCase("0")) {
            sliderStop.setVisibility(View.VISIBLE);
            sliderStart.setVisibility(View.GONE);
        } else {
            sliderStart.setVisibility(View.VISIBLE);
            sliderStop.setVisibility(View.GONE);
        }

        sliderStart.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                if(LocalHelper.isConnectedToInternet(TaskDetails.this)) {
                    sliderStart.setVisibility(View.GONE);
                    sliderStop.setVisibility(View.VISIBLE);
                    sliderStart.resetSlider();

                    call_statyus_update_API("inProgress", no_of_visits);
                }
                else {
                    Toast.makeText(TaskDetails.this, "No internet available", Toast.LENGTH_SHORT).show();
                }

            }
        });

        sliderStop.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                sliderStart.setVisibility(View.VISIBLE);
                sliderStop.setVisibility(View.GONE);
                sliderStop.resetSlider();

                if(sharedPreferences.getString("geofencetaskid","0").equalsIgnoreCase(jobId)) {
                    call_statyus_update_API("onHold", no_of_visits + 1);
                }
                else {
                    call_statyus_update_API("onHold", no_of_visits);
                }
            }
        });

        tvpening.setOnClickListener(this);
        imgMap.setOnClickListener(this);
        img1.setOnClickListener(this);
        tvupdate.setOnClickListener(this);
        imgback.setOnClickListener(this);
        img2.setOnClickListener(this);

        tvheader.setText("Task Details ");

        addImage = findViewById(R.id.iv_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addImage();
                selectImage(TaskDetails.this);
            }
        });



      /*  count++;
        numberVisit = String.valueOf(count);*/
        //  getImagelist(images);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocation();

       /* if (!sharedPreferences.getBoolean("workinprogress",false)) {
            show_status_update_dialogue();
        }*/

    }


    private void addImage() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.image, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
        parentLinearLayout.isFocusable();

        selectedImage = rowView.findViewById(R.id.number_edit_text);
        //  selectImage(TaskDetails.this);
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Choose a Media");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        addImage();
                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        selectedImage.setImageBitmap(img);
                        Picasso.get().load(getImageUri(TaskDetails.this, img)).into(selectedImage);

                        String imgPath = FileUtil.getPath(TaskDetails.this, getImageUri(TaskDetails.this, img));

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                        lldummyimageview.setVisibility(View.GONE);

                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        addImage();
                        Uri img = data.getData();
                        Picasso.get().load(img).into(selectedImage);

                        String imgPath = FileUtil.getPath(TaskDetails.this, img);

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                        lldummyimageview.setVisibility(View.GONE);


                    }
                    break;
            }
        }
    }

    //===== bitmap to Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "intuenty", null);
        Log.d("image uri", path);
        return Uri.parse(path);
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(TaskDetails.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            Log.d("data", String.valueOf(latitude));

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_pening:
                customDialg();
                break;

            /*case R.id.img_map:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }, 1000);
                break;*/

            case R.id.pic1:

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;

            case R.id.pic2:

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;


            case R.id.tv_update:

                  uploadImages();

                // call_statyus_update_API(tvpening.getText().toString().trim(),"200");


                //sendTheProfileFromServer();

//                AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
//                asyncTaskRunner.execute();
                break;

            case R.id.ivback:
                finish();
                break;

        }

    }


    private Object getHeaders() {
        Map<String, String> mHeaders = new HashMap<>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        return mHeaders;
    }


    public void customDialg() {
        final Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.alertdilaog);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvupdateSubmit = mDialog.findViewById(R.id.submitButton);
        TextView tvCancel = mDialog.findViewById(R.id.TvCancel);
        RadioGroup taskstatus = mDialog.findViewById(R.id.radiostatus);
        RadioButton radioButtonpeing = mDialog.findViewById(R.id.rdPening);
        RadioButton radioButtonworkInProgress = mDialog.findViewById(R.id.rdWorkinProgress);
        RadioButton radioButtonWorkOnHold = mDialog.findViewById(R.id.rdWorkOnHold);
        RadioButton radioButtoncompleted = mDialog.findViewById(R.id.rdCompleted);


      /*  taskstatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb ) {
                   // Toast.makeText(TaskDetails.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    tvpening.setText(rb.getText());
                    mDialog.cancel();
                }
            }
        });*/

        tvupdateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButtonpeing.isChecked()) {
                    tvpening.setText("Pending");
                } else if (radioButtonworkInProgress.isChecked()) {
                    tvpening.setText("inProgress");
                } else if (radioButtonWorkOnHold.isChecked()) {
                    tvpening.setText("onHold");
                } else if (radioButtoncompleted.isChecked()) {
                    tvpening.setText("completed");
                }
                mDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(getIntent());
            }
        });

        mDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }


    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(TaskDetails.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(TaskDetails.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  HTHCProgressBarUtil.showProgressBar(mProgressView,"Uploading the data");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String photoid = sharedPreferences.getString(Keys.PHOTO_ID_UPLOADED_FILE_URL_KEY, "");
            connectServer(photoid);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String postUrl = Constants.UPDATE_JOBS;  // need to be changed
            postRequest(postUrl, postBodyImage);

        }


    }


    public void connectServer(String selectedImagePath3) {

        byte[] byteArray = null;
        byte[] soundBytes = null;


        if (selectedImagePath3.length() != 0) {
            try {
                InputStream inputStream =
                        getContentResolver().openInputStream(Uri.fromFile(new File(filename)));

                soundBytes = new byte[inputStream.available()];
                soundBytes = toByteArray(inputStream);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String filename3 = selectedImagePath3.substring(selectedImagePath3.lastIndexOf("/") + 1);

        if (selectedImagePath3.length() == 0) {
            postBodyImage = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("job_id", jobId)
                    .addFormDataPart("job_assign_id", jobassignId)
                    .addFormDataPart("job_status", tvpening.getText().toString().trim())
                    .addFormDataPart("lat", String.valueOf(latitude))
                    .addFormDataPart("long", String.valueOf(longitude))
                    .addFormDataPart("description", edtDescription.getText().toString().trim())
                    .addFormDataPart("work_visit", "")
                    .addFormDataPart("photo", filename3, RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                    .build();
        }

    }


    private void sendTheProfileFromServer() {

        updatedStatus = tvpening.getText().toString().trim();

        if (updatedStatus.equalsIgnoreCase(newtaskStatus)) {
            Toast.makeText(TaskDetails.this, "Please choose the task status ", Toast.LENGTH_SHORT).show();
            return;
        } else {

            no_of_visits = Integer.parseInt(numberVisit) + 1;


            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userId);
                jsonObject.put("job_id", jobId);
                jsonObject.put("job_assign_id", jobassignId);
                jsonObject.put("job_status", tvpening.getText().toString().trim());
                jsonObject.put("lat", String.valueOf(latitude));
                jsonObject.put("long", String.valueOf(longitude));
                jsonObject.put("description", edtDescription.getText().toString().trim());
                jsonObject.put("work_visit", no_of_visits);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Log.d(Constants.LOG, Constants.UPDATE_JOBS);
            JsonObjectRequest mRequest = new JsonObjectRequest(Request.Method.POST, Constants.UPDATE_JOBS,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(Constants.LOG, response.toString());
                    progressDialog.dismiss();
                    try {
                        int status = response.getInt("status");
                        if (status == 1) {
                            isupdated = true;
                            String message = "Data Saved Sucessfully";

                            if (tvpening.getText().toString().trim().equalsIgnoreCase("inProgress")) {
                                mEditor.putBoolean("workinprogress", true);
                                mEditor.apply();

                            }
                            if (tvpening.getText().toString().trim().equalsIgnoreCase("onHold")) {
                                mEditor.putBoolean("workinprogress", false);
                                mEditor.apply();

                            }
                            showAlertDialogStay(message);
                            Intent intent = new Intent();
                            intent.putExtra(Keys.JOB_STATUS, "");
                            setResult(Activity.RESULT_OK, intent);
                            finish();
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
                    mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

                    return mHeaders;
                }

            };

            mRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            PlanetWorkVolleySingleton.getInstance(TaskDetails.this).addToRequestQueue(mRequest);

        }
    }

    private void showAlertDialogStay(String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog alertDialog = builder.create();
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
                finish();

            }
        });


        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        showToast("Something went wrong in server");
//                        HTHCProgressBarUtil.stopTheProgress(mProgressView);


                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            try {

                                //  HTHCProgressBarUtil.stopTheProgress(mProgressView);

                                String responseString = response.body().string();
                                Log.d("JSON SUCCESS RESPONSE", responseString);

                                JSONObject deliveryJSONObject = new JSONObject(responseString);
                                int status = deliveryJSONObject.getInt("status");
                                if (status == 1) {
                                    String message = "Data Saved Sucessfully";
                                    // showAlertDialogStay(message);

                                    Toast.makeText(getApplicationContext(), "Data Saved Sucessfully", Toast.LENGTH_SHORT).show();

//                                    Intent intent = new Intent();
//                                    intent.putExtra(Keys.CONSG_NUMBER,tvConsgDisplay.getText().toString());
//                                    setResult(Activity.RESULT_OK, intent);
                                    // finish();

                                } else {
                                    // AlertDialogShow.showSimpleAlert("Failed", deliveryJSONObject.getString("message"), getSupportFragmentManager());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    private void dispatchTakePictureIntent(Bitmap bitmap) {

        if (SDK_INT >= Build.VERSION_CODES.Q) {
            FileOutputStream fos;
            try {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "TestFolder");
                Uri imageuri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageuri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();


                img1.setImageBitmap(bitmap);
                // img2.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            takenPicture = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (takenPicture == null) {
                return;
            }


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takenPicture));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CAMERA);

            }
        }
    }

    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {


            File mediaStorageDir = new File(getExternalFilesDir(null), "PlanetWork");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {

                    return null;
                }
            }


            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {

            } else {
                return null;
            }
        } else {

            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(getCacheDir() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            } else {
                return null;
            }

        }

        return mediaFile;
    }


    /* @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         switch (requestCode) {
             case CAMERA_REQUEST:
                 if (resultCode == Activity.RESULT_OK) {
                     if (SDK_INT > Build.VERSION_CODES.Q){
                         Bitmap photo = (Bitmap) data.getExtras().get("data");
                         dispatchTakePictureIntent(photo);


                     }else{
                         BackgroundSavingImage backgroundSavingImage = new BackgroundSavingImage();
                         backgroundSavingImage.execute();
                     }
                 }
                 break;

         }
     }
 */
    private class BackgroundSavingImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  HTHCProgressBarUtil.showProgressBar(mProgressView,"Saving Image in Local Storage");
        }

        @Override
        protected Void doInBackground(Void... voids) {

//            File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
//            try  {
//                FileOutputStream out = new FileOutputStream(file);
//                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            compressImage(takenPicture.getAbsolutePath());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // imageTick.setVisibility(View.VISIBLE);
            //  HTHCProgressBarUtil.stopTheProgress(mProgressView);

        }
    }

    public void compressImage(String filepathh) {

        String filePath = getRealPathFromURI(filepathh);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(filepathh, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);


        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);


        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;


        filename = getFilename();


        try {
            out = new FileOutputStream(filename);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putString(Keys.PHOTO_ID_UPLOADED_FILE_URL_KEY, filename);
        mEditor.apply();

    }

    public String getFilename() {
        File file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        String uriSting = (file.getAbsolutePath());
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

       /* if(isupdated){
            HomeFragment fragment=new HomeFragment();
            Bundle b=new Bundle();
            b.putString("reload","0");
            fragment.setArguments(b);
        }
        else {
            Intent intent=new Intent(TaskDetails.this , DashBoardActivity.class);
            intent.putExtra("reload","0");
            startActivity(intent);
        }*/
    }

    private void getImagelist(String images) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(images);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imageurl = jsonObject.getString("img");

                TaskImagesDAO imagesDAO = new TaskImagesDAO();
                imagesDAO.setImageurl(imageurl);

                taskimagelist.add(imagesDAO);

               /* jsonArray1 = jsonArray.getJSONArray(i);

                for (int j = 0; j < jsonArray1.length(); j++) {

                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                    String imageurl = jsonObject.getString("img");

                    TaskImagesDAO imagesDAO = new TaskImagesDAO();
                    imagesDAO.setImageurl(imageurl);

                    taskimagelist.add(imagesDAO);

                }*/

                //  Log.v("TAG","taskimagelist images"+taskimagelist);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray.length() > 0) {
            rvImagelist.setLayoutManager(new LinearLayoutManager(TaskDetails.this, LinearLayoutManager.HORIZONTAL, false));
            adapterTaskImages = new AdapterTaskImages(TaskDetails.this, taskimagelist);
            rvImagelist.setAdapter(adapterTaskImages);
            llimagelist.setVisibility(View.VISIBLE);
        } else {
            llimagelist.setVisibility(View.GONE);
        }

    }

    private void show_status_update_dialogue() {
        new MaterialAlertDialogBuilder(TaskDetails.this)
                .setTitle("PlanetWork")
                .setMessage("you are ready to work on " + newTask + " ?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        //   call_statyus_update_API("Work In Progress","100");
                        //call_statyus_update_API();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        onBackPressed();
                    }
                })
                .show();
    }

    private void call_statyus_update_API(String jobstatus, int visits) {

        HashMap<String, String> mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Accept", "application/json");
        mHeaders.put("Authorization", "Bearer " + sharedPreferences.getString(Keys.ACCESS_TOKEN_KEY, ""));

        HashMap<String, Object> listDetails = new HashMap<>();
        //  listDetails.put(Constants.USERID, sharedPreferences.getString(Constants.userIdKey, ""));
        // listDetails.put(Constants.JOBID, jobId);
        listDetails.put(Constants.JOBASSIGNID, jobassignId);
        listDetails.put("status", jobstatus);
        listDetails.put("visits", visits);
        //  listDetails.put("status", "Work On Hold");

        retrofit2.Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().updatetaskstatus(mHeaders, listDetails);
        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                try {

                    String result = new Gson().toJson(response.body());
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1")) {

                        newtaskStatus = jobstatus;
                        tvpening.setText(newtaskStatus);
                        if (jobstatus.equalsIgnoreCase("inProgress")) {
                            String taskmessage = newTask + " task has been started.click to know more";

                            mEditor.putString("job_id", jobId);
                            mEditor.putBoolean("workinprogress", true);
                            mEditor.putBoolean("onduty", true);

                           /* double lattitude= Double.parseDouble(sharedPreferences.getString("mylat","0.0"));
                            double longitude= Double.parseDouble(sharedPreferences.getString("mylong","0.0"));

                            if (distance(lattitude, longitude, Double.parseDouble(newlat), Double.parseDouble(newlong)) < 0.1) { // if distance < 0.1
                                Log.v("TAG","Inside Geofence");
                                //   launch the activity
                            }
                            else {
                                Log.v("TAG","Outside Geofence");
                            }
*/

                            Intent geofenceintent = new Intent(TaskDetails.this, GeofencingService.class);
                            geofenceintent.putExtra("lattitude", newlat);
                            geofenceintent.putExtra("longitude", newlong);

                           /* geofenceintent.putExtra("lattitude","13.0212406");
                            geofenceintent.putExtra("longitude","77.6472387");*/
                            startService(geofenceintent);

                            LocalHelper.showNotificationwithlatlong(TaskDetails.this, "Task Started", taskmessage, newlat, newlong, "true");

                        } else {
                            String taskmessage = newTask + " task has been Ended.";
                            mEditor.putString("job_id", "0");
                            mEditor.putBoolean("workinprogress", false);
                            mEditor.putBoolean("onduty", true);
                            mEditor.putString("geofencetaskid","null");
                            mEditor.putBoolean("insideGeofence",false);

                            Intent geofenceintent = new Intent(TaskDetails.this, GeofencingService.class);
                            stopService(geofenceintent);

                           LocalHelper.showNotification(TaskDetails.this, "Task Ended", taskmessage);

                        }

                        mEditor.apply();
                        // onBackPressed();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                // Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("TAG", "Retrofit error " + t.getMessage());
            }
        });

    }

    private void uploadImages() {

        String description=edtDescription.getEditableText().toString().trim();
        String totalVisits= String.valueOf(no_of_visits+1);

        List<MultipartBody.Part> list = new ArrayList<>();
        retrofit2.Call<ApiModel> call;

        for (Uri uri:files) {

            Log.i("uris",uri.getPath());

            list.add(prepareFilePart("file[]", uri));
        }

        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);

        if(list.size()>0)
        {
            call = serviceInterface.uploadtaskDetailswithImages(list,userId,jobId,newtaskStatus,description,totalVisits,jobassignId);

        }
        else {
            call = serviceInterface.uploadTaskdetails(userId,jobId,newtaskStatus,description,totalVisits,jobassignId);

        }

         call.enqueue(new retrofit2.Callback<ApiModel>() {
            @Override
            public void onResponse(retrofit2.Call<ApiModel> call, retrofit2.Response<ApiModel> response) {

                try {

                    ApiModel addMediaModel = response.body();
                    if(addMediaModel.getStatus().equalsIgnoreCase("success")){
                        Toast.makeText(TaskDetails.this, "Files uploaded successfuly", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("main", "the status is ----> " + addMediaModel.getStatus());
                    Log.e("main", "the message is ----> " + addMediaModel.getFile_upload());

                }
                catch (Exception e){
                    Log.d("Exception","|=>"+e.getMessage());
//
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiModel> call, Throwable t) {

                Log.i("my",t.getMessage());
            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(fileUri.getPath());
        Log.i("here is error", file.getAbsolutePath());
        // create RequestBody instance from file

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);


    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometers

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

}
