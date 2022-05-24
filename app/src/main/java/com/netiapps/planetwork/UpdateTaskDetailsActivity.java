package com.netiapps.planetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.netiapps.planetwork.model.ApiModel;
import com.netiapps.planetwork.retrofit.ApiConstants;
import com.netiapps.planetwork.retrofit.ServiceInterface;
import com.netiapps.planetwork.utils.FileUtil;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskDetailsActivity extends AppCompatActivity {

    ImageView selectedImage;
    CircularProgressButton btnSubmit;
    ServiceInterface serviceInterface;
    List<Uri> files = new ArrayList<>();

    private LinearLayout parentLinearLayout;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task_details);


        parentLinearLayout= findViewById(R.id.parent_linear_layout);

        ImageView addImage = findViewById(R.id.iv_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        btnSubmit = findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //requestPermission();
                uploadImages();
            }
        });
    }

    public void addImage() {
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.image, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
       // parentLinearLayout.isFocusable();

        selectedImage = rowView.findViewById(R.id.number_edit_text);
        selectImage(UpdateTaskDetailsActivity.this);
    }

    //===== select image
    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        selectedImage.setImageBitmap(img);
                        Picasso.get().load(getImageUri(UpdateTaskDetailsActivity.this,img)).into(selectedImage);

                        String imgPath = FileUtil.getPath(UpdateTaskDetailsActivity.this,getImageUri(UpdateTaskDetailsActivity.this,img));

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        Picasso.get().load(img).into(selectedImage);

                        String imgPath = FileUtil.getPath(UpdateTaskDetailsActivity.this,img);

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);

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
        Log.d("image uri",path);
        return Uri.parse(path);
    }

    public void uploadImages(){

        btnSubmit.startAnimation();

        List<MultipartBody.Part> list = new ArrayList<>();

        for (Uri uri:files) {

            Log.i("uris",uri.getPath());

            list.add(prepareFilePart("file[]", uri));
        }

        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);

        Call<ApiModel> call = serviceInterface.uploadtaskDetailswithImages(list,"3","1","100","3 images","5","1");
        call.enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                btnSubmit.revertAnimation();
                try {

                    ApiModel addMediaModel = response.body();
                    if(addMediaModel.getStatus().equals("200")){
                        Toast.makeText(UpdateTaskDetailsActivity.this, "Files uploaded successfuly", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ApiModel> call, Throwable t) {
                btnSubmit.revertAnimation();
                Log.i("my",t.getMessage());
            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(fileUri.getPath());
        Log.i("here is error",file.getAbsolutePath());
        // create RequestBody instance from file

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }


}