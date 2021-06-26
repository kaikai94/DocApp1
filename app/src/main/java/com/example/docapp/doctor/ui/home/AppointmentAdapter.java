package com.example.docapp.doctor.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docapp.R;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    //String data1[], data2[];

    List<String> dateList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<String> appointmentIDList = new ArrayList<>();

    Context context;

    public AppointmentAdapter(Context ct, List<String> dateList, List<String> timeList, List<String> appointmentIDList){
        this.context = ct;
        this.dateList = dateList;
        this.timeList = timeList;
        this.appointmentIDList = appointmentIDList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.doctor_home_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder holder, int position)
    {
        holder.dateTextView.setText(dateList.get(position));
        holder.timeTextView.setText(timeList.get(position));
        holder.appointmentIDTextView.setText(appointmentIDList.get(position));
    }

    @Override
    public int getItemCount() {
        return appointmentIDList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView, timeTextView, appointmentIDTextView;
        Button modifyBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            appointmentIDTextView = itemView.findViewById(R.id.AppointmentIDTextView);
        }
    }
}
