package com.netiapps.planetwork.mvvm;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.TaskDetails;
import com.netiapps.planetwork.model.TasksDAO;
import com.netiapps.planetwork.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterHomeTasksmvvm extends RecyclerView.Adapter<AdapterHomeTasksmvvm.ViewHolder> implements RecyclerView.OnItemTouchListener {

    private List<TasksDAO> myTasklist = new ArrayList<>();
    private Context context;
    int index;
    SharedPreferences sharedPreferences;
    private OnItemClickListener mListener;

    public AdapterHomeTasksmvvm(Context context, List<TasksDAO> myTasklist) {
        this.context=context;
        this.myTasklist=myTasklist;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    GestureDetector mGestureDetector;

    public AdapterHomeTasksmvvm(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }


    @NonNull
    @Override
    public AdapterHomeTasksmvvm.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false);
        return new AdapterHomeTasksmvvm.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHomeTasksmvvm.ViewHolder holder, int position) {


        sharedPreferences = (SharedPreferences) context.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);

        final TasksDAO homeData = myTasklist.get(position);

        String no_of_visits=homeData.getNo_of_visit().trim();



        holder.tvdate.setText(homeData.getJob_date());
        // holder.tvcompnayJob.setText(homeData.getCustomer_name());
        holder.tvbrnchSub.setText(homeData.getBranch());
        holder.tvcount.setText(homeData.getNo_of_visit());
        holder.tvTaskName.setText(homeData.getTask());
        holder.tvSRnumber.setText("SR NO : "+homeData.getSr_no());

       // String images = String.valueOf(homeData.getImages());

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imagelist", homeData.getImages());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ("pending".equalsIgnoreCase(homeData.getJob_status())){
            holder.tvPending.setText(Constants.TASK_PENDING);
            holder.tvPending.setBackgroundResource(R.drawable.border_button);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.pending));
        } else  if("Work In Progress".equalsIgnoreCase(homeData.getJob_status())){
            holder.tvPending.setText(Constants.TASK_IN_PROGRESS);
            holder.tvPending.setBackgroundResource(R.drawable.complerejected);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.inprogress));
        } else  if("Completed".equalsIgnoreCase(homeData.getJob_status())) {
            holder.tvPending.setText(Constants.TASK_COMPLETED);
            holder.tvPending.setBackgroundResource(R.drawable.completed_back);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.completed));
        }
        else if("Work On Hold".equalsIgnoreCase(homeData.getJob_status())) {
            holder.tvPending.setText(Constants.TASK_IN_HOLD);
            holder.tvPending.setBackgroundResource(R.drawable.onhold_back);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.onhold));
        }

        holder.taskdatalayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = holder.getAdapterPosition();
                notifyDataSetChanged();

                String imagelist= String.valueOf(homeData.getImages());

                if(sharedPreferences.getString("session_loggedin","0").equalsIgnoreCase("1"))
                {
                    Intent itent = new Intent(context, TaskDetails.class);
                itent.putExtra("Date",homeData.getJob_date());
                itent.putExtra("Copany",homeData.getCustomer_name());
                itent.putExtra("task",homeData.getTask());
                itent.putExtra("Branch",homeData.getBranch());
                itent.putExtra("JobAssignId",homeData.getJob_assign_id());
                itent.putExtra("NoOfVisit",homeData.getNo_of_visit());
                itent.putExtra("JobId",homeData.getJob_id());
                itent.putExtra("task_status",homeData.getTask_status());
                itent.putExtra("sr_no",homeData.getSr_no());
                itent.putExtra("position",index);
                itent.putExtra("images", imagelist);

                context.startActivity(itent);
                }
                else {
                    Show_login_popup();
                         }
            }

        });

        holder.rllocationlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat=homeData.getLatitude();
                String lang=homeData.getLongitude();
                // Uri gmmIntentUri = Uri.parse("google.navigation:q=12.9767,77.5713");
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lang);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
        if (index == position)
        {
            holder.cardlayout.setCardBackgroundColor(context.getResources().getColor(R.color.selector));

        }
        else {
            holder.cardlayout.setCardBackgroundColor(context.getResources().getColor(R.color.white));

        }

    }

    private void Show_login_popup() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("PlanetWork")
                .setMessage("NOTE : You must Login to View / Update your task ")
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


    @Override
    public int getItemCount() {
        return myTasklist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvdate;
        TextView tvbrnchSub;
        TextView tvcompnayJob;
        TextView tvPending;
        TextView tvcount;
        TextView tvTaskName;
        TextView tvSRnumber;
        RelativeLayout taskdatalayout;
        View itemView;
        CardView cardlayout;
        LinearLayout rllocationlayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvdate = (TextView) itemView.findViewById(R.id.tvdate);
            // tvcompnayJob = (TextView) itemView.findViewById(R.id.tvcompnayJob);
            tvbrnchSub = (TextView) itemView.findViewById(R.id.tvbrnchSub);
            tvPending= (TextView) itemView.findViewById(R.id.tvPending);
            tvcount = (TextView) itemView.findViewById(R.id.tvcount);
            taskdatalayout = itemView.findViewById(R.id.rldatalayout);
            tvTaskName=itemView.findViewById(R.id.tvTaskanme);
            rllocationlayout=itemView.findViewById(R.id.llviewlocation);
            cardlayout=itemView.findViewById(R.id.card_holder);
            tvSRnumber=itemView.findViewById(R.id.tvsrnumber);

        }


    }
}
