package com.netiapps.planetwork.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.TaskDetails;
import com.netiapps.planetwork.model.TaskImagesDAO;
import com.netiapps.planetwork.model.TasksDAO;
import com.netiapps.planetwork.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterTaskImages extends RecyclerView.Adapter<AdapterTaskImages.ViewHolder>  {

    ArrayList<TaskImagesDAO>taskimagelist=new ArrayList<>();

    private Context context;
    private String images;
    int index;
    SharedPreferences sharedPreferences;

    public AdapterTaskImages(TaskDetails taskDetails, ArrayList<TaskImagesDAO> taskimagelist) {
        this.context=taskDetails;
        this.taskimagelist=taskimagelist;

    }

    @NonNull
    @Override
    public AdapterTaskImages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_images, parent, false);

        return new AdapterTaskImages.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTaskImages.ViewHolder holder, int position) {

        final TaskImagesDAO imageurl = taskimagelist.get(position);
        String url=imageurl.getImageurl();
        Picasso.get().load(url).into(holder.ivtaskimages);

    }


    @Override
    public int getItemCount() {
        return taskimagelist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivtaskimages;

        View itemView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            ivtaskimages =  itemView.findViewById(R.id.ivimages);

        }


    }
}
