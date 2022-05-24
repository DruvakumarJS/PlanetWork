package com.netiapps.planetwork.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.netiapps.planetwork.GoogleMapActivity;
import com.netiapps.planetwork.R;
import com.netiapps.planetwork.model.AttendenceModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterAttendence extends RecyclerView.Adapter<AdapterAttendence.ViewHolder>  {

    private List<AttendenceModel> myTasklist = new ArrayList<>();
    private Context context;
    int index;

    public AdapterAttendence(Context context, List<AttendenceModel> myTasklist) {
        this.context=context;
        this.myTasklist=myTasklist;
    }

    @NonNull
    @Override
    public AdapterAttendence.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendence_item_layout, parent, false);
        return new AdapterAttendence.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAttendence.ViewHolder holder, int position) {

       final AttendenceModel attendenceModel = myTasklist.get(position);

       String isHoliday =attendenceModel.getIs_holiday();
       String isWeekend = attendenceModel.getIs_weekend();
       String onLeave=attendenceModel.getIs_leave();
       String dayname=attendenceModel.getDay();
       String date=attendenceModel.getDate();
       String split_dayname=dayname.substring(0, 3);;

        String monthh="";
        try {
            DateFormat monthname=new SimpleDateFormat("LLL dd");
            DateFormat datformat=new SimpleDateFormat("dd-MM-yyyy");

            Date date1=datformat.parse(date);
            monthh=monthname.format(date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        int totalminutes=Integer.parseInt(attendenceModel.getWorking_hours());

        int hours = (totalminutes/60);
        int min= (totalminutes % 60);

     /*   holder.login.setText(attendenceModel.getLogin());
        holder.logout.setText(attendenceModel.getLogout());
        holder.hours.setText(hours + "h " + min + "m");
        holder.day.setText(split_dayname);
        if (attendenceModel.getLogin().equalsIgnoreCase("0")) {
            holder.tvMap.setVisibility(View.INVISIBLE);
        }*/

        if(isHoliday.equalsIgnoreCase("true") && attendenceModel.getLogin().equalsIgnoreCase("0"))
        {
            holder.date.setText(monthh);
            holder.logout.setText("");
            holder.login.setText(attendenceModel.getHoliday_title());
            holder.hours.setText("");
            holder.day.setText(split_dayname);
            holder.tvMap.setVisibility(View.INVISIBLE);

        }
        else if(isHoliday.equalsIgnoreCase("true") && !attendenceModel.getLogin().equalsIgnoreCase("0"))
        {
            holder.date.setText(monthh);
            holder.login.setText(attendenceModel.getLogin());
            holder.logout.setText(attendenceModel.getLogout());
            holder.hours.setText(hours + "h " + min + "m");
            holder.day.setText(split_dayname);
            holder.tvMap.setVisibility(View.VISIBLE);

        }
        else {
            holder.date.setText(monthh);
            holder.login.setText(attendenceModel.getLogin());
            holder.logout.setText(attendenceModel.getLogout());
            holder.hours.setText(hours + "h " + min + "m");
            holder.day.setText(split_dayname);
            if (attendenceModel.getLogin().equalsIgnoreCase("0")) {
                holder.tvMap.setVisibility(View.INVISIBLE);
            }
            else {
                holder.tvMap.setVisibility(View.VISIBLE);
            }

        }


        if(isHoliday.equalsIgnoreCase("true")){
            holder.llattendance.setBackgroundColor(context.getResources().getColor(R.color.blue));
        }
        else  if(isWeekend.equalsIgnoreCase("true")){
            holder.llattendance.setBackgroundColor(context.getResources().getColor(R.color.red));
        }
        else  if(onLeave.equalsIgnoreCase("true")){
            holder.llattendance.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }
        else {
            holder.llattendance.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GoogleMapActivity.class);
                intent.putExtra("start_time",attendenceModel.getLogin());
                intent.putExtra("end_time",attendenceModel.getLogout());
                intent.putExtra("date",attendenceModel.getDate());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myTasklist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView login;
        TextView logout;
        TextView hours;
        TextView day;
        TextView tvMap;
        LinearLayout llattendance;

        View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            date = (TextView) itemView.findViewById(R.id.tvdate);
            login = (TextView) itemView.findViewById(R.id.tvlogin);
            logout = (TextView) itemView.findViewById(R.id.tvlogout);
            hours = (TextView) itemView.findViewById(R.id.tvhours);
            llattendance=itemView.findViewById(R.id.llatendance);
            day=itemView.findViewById(R.id.tvday);
            tvMap=itemView.findViewById(R.id.tvmap);




        }


    }



}
