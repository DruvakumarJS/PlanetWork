package com.netiapps.planetwork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.model.NotificationModel;
import com.netiapps.planetwork.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.ViewHolder>  {

    private List<NotificationModel> myTasklist = new ArrayList<>();
    private Context context;
    int index;

    public AdapterNotifications(Context context, List<NotificationModel> myTasklist) {
        this.context=context;
        this.myTasklist=myTasklist;
    }


    @NonNull
    @Override
    public AdapterNotifications.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new AdapterNotifications.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotifications.ViewHolder holder, int position) {

       final NotificationModel NotificationModelData = myTasklist.get(position);

        String notificationiid=NotificationModelData.getId().trim();

        holder.tvtitttle.setText(NotificationModelData.getTittle());
        // holder.tvcompnayJob.setText(NotificationModelData.getCustomer_name());
        holder.tvdescription.setText(NotificationModelData.getDescription());
        holder.tvdate.setText(NotificationModelData.getDate());




    }

    @Override
    public int getItemCount() {
        return myTasklist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView tvtitttle;
        TextView tvdescription;
        TextView tvdate;

        View itemView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvtitttle = (TextView) itemView.findViewById(R.id.tv_notification_tittle);

            tvdescription= (TextView) itemView.findViewById(R.id.tv_notification_description);

            tvdate=itemView.findViewById(R.id.tvdate);


        }


    }
}
