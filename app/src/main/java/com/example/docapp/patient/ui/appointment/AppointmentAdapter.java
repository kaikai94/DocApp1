package com.example.docapp.patient.ui.appointment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docapp.R;
import com.example.docapp.patient.ui.PaymentFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    private static String TAG = "Patient appointment adapter";
    List<String> dateList;
    List<String> timeList;
    List<String> appointmentIDList;
    List<String> statusList;
    int layout;
    String layout_type;
    Context context;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public AppointmentAdapter(Context ct, List<String> dateList, List<String> timeList, List<String> statusList, List<String> appointmentIDList, String type){
        this.context = ct;
        this.dateList = dateList;
        this.timeList = timeList;
        this.appointmentIDList = appointmentIDList;
        this.statusList = statusList;
        this.layout = R.layout.my_row;
        layout_type = type;
    }

    public AppointmentAdapter(Context ct, final int layout, List<String> dateList, List<String> timeList, List<String> statusList, List<String> appointmentIDList){
        this.context = ct;
        this.dateList = dateList;
        this.timeList = timeList;
        this.appointmentIDList = appointmentIDList;
        this.statusList = statusList;

        if(layout != 0)
            this.layout = layout;
        else
            this.layout = R.layout.my_row;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder holder, int position)
    {
        holder.dateTextView.setText(dateList.get(position));
        holder.timeTextView.setText(timeList.get(position));
        holder.statusTextView.setText(statusList.get(position));
        holder.appointmentIDTextView.setText(appointmentIDList.get(position));

        /*holder.modifyBtn.setOnClickListener(v -> {
           Intent intent = new Intent(context, AppointmentActivity.class);
            intent.putExtra("data1", dateList.get(position));
            intent.putExtra("data2", timeList.get(position));
            intent.putExtra("data3", statusList.get(position));
            context.startActivity(intent);
            //Toast.makeText(context, "clicked on " +position, Toast.LENGTH_SHORT).show();

           Context context = v.getContext();
            Intent intent = new Intent(context, AppointmentActivity.class);
            intent.putExtra("data1", l1.get(position));

            context.startActivity(intent);
        });*/

        holder.modifyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppointmentActivity.class);
            intent.putExtra(context.getResources().getString(R.string.appointment_id), appointmentIDList.get(position));
            context.startActivity(intent);
        });

        switch(layout_type)
        {
            case "Pending": {
                holder.payBtn.setOnClickListener(v -> {
                    PaymentFragment frag = new PaymentFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString(context.getResources().getString(R.string.appointment_id), appointmentIDList.get(position));
                    frag.setArguments(bundle);

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment_content_patient_navigation, frag)
                            .addToBackStack(null)
                            .commit();
                });


                break;
            }
            case "Upcoming":
            {
                holder.payBtn.setVisibility(View.GONE);
                break;
            }
            case "History":
            {
                holder.modifyBtn.setText("View");
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return appointmentIDList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView, timeTextView, statusTextView, appointmentIDTextView;
        Button modifyBtn, payBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            appointmentIDTextView = itemView.findViewById(R.id.appointmentIDTextView);
            modifyBtn = itemView.findViewById(R.id.modifyBtn);
            payBtn = itemView.findViewById(R.id.payBtn);
        }
    }
}
