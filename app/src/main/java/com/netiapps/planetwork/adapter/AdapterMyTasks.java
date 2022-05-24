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
import com.netiapps.planetwork.model.Home;
import com.netiapps.planetwork.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AdapterMyTasks extends RecyclerView.Adapter<AdapterMyTasks.ViewHolder>  {

    private List<Home> myTasklist = new ArrayList<>();
    private Context context;
    int index;

    public AdapterMyTasks(Context context, List<Home> myTasklist) {
        this.context=context;
        this.myTasklist=myTasklist;
    }


    @NonNull
    @Override
    public AdapterMyTasks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false);
        return new AdapterMyTasks.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyTasks.ViewHolder holder, int position) {

       final Home homeData = myTasklist.get(position);

        String no_of_visits=homeData.getNo_of_visit().trim();

        holder.tvdate.setText(homeData.getJob_date());
        // holder.tvcompnayJob.setText(homeData.getCustomer_name());
        holder.tvbrnchSub.setText(homeData.getBranch());
        holder.tvcount.setText(homeData.getNo_of_visit());
        holder.tvTaskName.setText(homeData.getTask());
        holder.tvSRnumber.setText("SR NO : "+homeData.getSr_no());

        String jobStatus=homeData.getJob_status();

        if ("pending".equalsIgnoreCase(homeData.getJob_status())){
            holder.tvPending.setText(Constants.TASK_PENDING);
            holder.tvPending.setBackgroundResource(R.drawable.border_button);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.pending));
        } else  if("inProgress".equalsIgnoreCase(homeData.getJob_status())){
            holder.tvPending.setText(Constants.TASK_IN_PROGRESS);
            holder.tvPending.setBackgroundResource(R.drawable.complerejected);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.inprogress));
        } else  if("completed".equalsIgnoreCase(homeData.getJob_status())) {
            holder.tvPending.setText(Constants.TASK_COMPLETED);
            holder.tvPending.setBackgroundResource(R.drawable.completed_back);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.completed));
        }
        else if("onHold".equalsIgnoreCase(homeData.getJob_status())) {
            holder.tvPending.setText(Constants.TASK_IN_HOLD);
            holder.tvPending.setBackgroundResource(R.drawable.onhold_back);
            holder.tvPending.setTextColor(context.getResources().getColor(R.color.onhold));
        }

        holder.cardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = holder.getAdapterPosition();
                notifyDataSetChanged();
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
        RelativeLayout holdercardview;
        View itemView;
        CardView cardlayout;
        RelativeLayout rllocationlayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvdate = (TextView) itemView.findViewById(R.id.tvdate);
            // tvcompnayJob = (TextView) itemView.findViewById(R.id.tvcompnayJob);
            tvbrnchSub = (TextView) itemView.findViewById(R.id.tvbrnchSub);
            tvPending= (TextView) itemView.findViewById(R.id.tvPending);
            tvcount = (TextView) itemView.findViewById(R.id.tvcount);
            holdercardview = itemView.findViewById(R.id.rldatalayout);
            tvTaskName=itemView.findViewById(R.id.tvTaskanme);
            rllocationlayout=itemView.findViewById(R.id.lllocation);
            cardlayout=itemView.findViewById(R.id.card_holder);
            tvSRnumber=itemView.findViewById(R.id.tvsrnumber);

        }


    }
}
