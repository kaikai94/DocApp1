package com.example.docapp.doctor.ui.appointment;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.docapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    //String data1[], data2[];

    private final static String TAG = "Appointment";
    List<String> dateList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<String> appointmentIDList = new ArrayList<>();
    List<String> statusList = new ArrayList<>();
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    Context context;

    public AppointmentAdapter(Context ct, List<String> dateList, List<String> timeList, List<String> statusList, List<String> appointmentIDList){
        this.context = ct;
        this.dateList = dateList;
        this.timeList = timeList;
        this.appointmentIDList = appointmentIDList;
        this.statusList = statusList;
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row2, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.dateTextView.setText(dateList.get(position));
        holder.timeTextView.setText(timeList.get(position));
        holder.statusTextView.setText(statusList.get(position));
        holder.appointmentIDTextView.setText(appointmentIDList.get(position));

        holder.approveBtn.setOnClickListener(v -> fStore
            .collection(context.getString(R.string.collection_appointments)).document(appointmentIDList.get(position))
            .update(context.getString(R.string.status), context.getString(R.string.status_approve))
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Successfully Approve!", Toast.LENGTH_SHORT);
            })
            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e)));

        holder.declineBtn.setOnClickListener(v -> fStore
            .collection(context.getString(R.string.collection_appointments)).document(appointmentIDList.get(position))
            .update(context.getString(R.string.status), context.getString(R.string.status_decline))
            .addOnSuccessListener(aVoid -> {
                //Log.d(TAG, "Successfully updated!");
                Toast.makeText(context, "Successfully Decline!", Toast.LENGTH_SHORT);
            })
            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e)));
    }

    @Override
    public int getItemCount() {
        return appointmentIDList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateTextView, timeTextView, statusTextView, appointmentIDTextView;
        Button approveBtn, declineBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            appointmentIDTextView = itemView.findViewById(R.id.appointmentIDTextView);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            declineBtn = itemView.findViewById(R.id.declineBtn);
        }
    }
}
