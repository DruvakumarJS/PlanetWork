package com.netiapps.planetwork.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netiapps.planetwork.R;
import com.netiapps.planetwork.ReportActivity;
import com.netiapps.planetwork.ReportListActivity;
import com.netiapps.planetwork.model.ReportModel;
import com.netiapps.planetwork.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterTravelHistory extends RecyclerView.Adapter<AdapterTravelHistory.ViewHolder> {

    private Context context;
    private List<ReportModel> itemList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    public AdapterTravelHistory(Context context, List<ReportModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reportsinglelist, parent, false);
        return new AdapterTravelHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ReportModel reportModel = itemList.get(position);

        sharedPreferences = (SharedPreferences) context.getSharedPreferences(Constants.sharedPreferencesKey, MODE_PRIVATE);


        holder.tvDate.setText(reportModel.getDate());
        holder.tvTime.setText(reportModel.getStart_time());
        holder.tvsrno.setText(reportModel.getSrno());
        holder.tvendTime.setText(" - "+reportModel.getEnd_time());
        holder.tvdistance.setText(reportModel.getDistance()+" km");
        holder.tvaddress.setText("From: "+reportModel.getFrom_address());
        holder.tvtoaddress.setText("To :"+reportModel.getTo_address());



        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportModel.getDistance().equalsIgnoreCase("0")) {
                    Intent intent = new Intent(context, ReportActivity.class);
                    intent.putExtra("userId", sharedPreferences.getString(Constants.userIdKey, ""));
                    intent.putExtra("date", reportModel.getDate());
                    intent.putExtra("start_time", reportModel.getStart_time());
                    intent.putExtra("end_time", reportModel.getEnd_time());
                    intent.putExtra("distance",reportModel.getDistance());
                    intent.putExtra("from_address",reportModel.getFrom_address());
                    intent.putExtra("to_address",reportModel.getTo_address());

                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Not enough data to show on map", Toast.LENGTH_SHORT).show();
                }
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
        TextView tvDate;
        TextView tvdistance;
        TextView tvaddress;
        TextView tvTime;
        TextView tvendTime;
        TextView tvtoaddress;
        TextView tvsrno;
        LinearLayout linearLayout;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvaddress = (TextView) itemView.findViewById(R.id.tvaddress);
            tvdistance = (TextView) itemView.findViewById(R.id.tvdistance);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            linearLayout = itemView.findViewById(R.id.llinerlayout);
            tvendTime = (TextView) itemView.findViewById(R.id.tvendTime);
            tvtoaddress= (TextView) itemView.findViewById(R.id.tvtoaddress);
            tvsrno = (TextView) itemView.findViewById(R.id.tvsrno);

        }
    }

    public void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler,final String addresType) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List <Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);

                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getAddressLine(0));

                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        bundle.putString("address_type", addresType);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = " Unable to get address for this location.";
                        bundle.putString("address", result);
                        bundle.putString("address_type", addresType);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
