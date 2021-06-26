package com.example.docapp.patient.ui.appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.docapp.R;
import com.example.docapp.classes.DoctorSpeciality;
import com.example.docapp.classes.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.docapp.databinding.ActivityAppointmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {
    TextInputEditText nameText, appointmentDateText;
    MaterialAutoCompleteTextView specialityDrpDown, appointmentTimeDrpDown;
    Button update_btn, cancel_btn;

    private static String TAG = "Appointment Activity";
    private ActivityAppointmentBinding binding;
    List<DoctorSpeciality> specialityList = new ArrayList<>();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String appointmentId;
    //ArrayAdapter<CharSequence> arrayAdapter;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this , R.array.timeslot, android.R.layout.simple_list_item_1);
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.timeslot));
        nameText = binding.aaNameText;
        specialityDrpDown = binding.aaSpecialityDrpDown;
        appointmentDateText = binding.aaApointmentDatePicker;
        appointmentTimeDrpDown = binding.aaAppointmentTimeDrpDown;
        update_btn = binding.updateBtn;
        cancel_btn = binding.cancelBtn;

        //Get string array and set adapter to dropdown
        appointmentTimeDrpDown.setAdapter(arrayAdapter);

        List<String> tempList = new ArrayList<>();

        Utilities utilities = Utilities.getInstance();

        utilities.getSpecialityList(list -> {
            specialityList = list;
            tempList.clear();
            list.forEach(cat -> tempList.add(cat.getName()));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tempList);
            specialityDrpDown.setAdapter(adapter);
            setData(adapter);
        });


        if(getIntent().hasExtra(getString(R.string.history)))
        {
            update_btn.setVisibility(View.GONE);
            cancel_btn.setVisibility(View.GONE);
            nameText.setEnabled(false);
        }
        else if(getIntent().hasExtra(getString(R.string.modify)))
        {
            update_btn.setVisibility(View.VISIBLE);
            cancel_btn.setVisibility(View.VISIBLE);
            nameText.setEnabled(true);

        }

        update_btn.setOnClickListener(v -> {

        });
    }

    //Get data online and bind
    private void setData(ArrayAdapter<String> adapter)
    {
        if(getIntent().hasExtra(getString(R.string.appointment_id)))
        {
            appointmentId = getIntent().getStringExtra(getResources().getString(R.string.appointment_id));

            fStore.collection(getResources().getString(R.string.collection_appointments))
                    .document(appointmentId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    nameText.setText(document.getString(getString(R.string.name)));
                                    DateTime dt = new DateTime( document.getTimestamp(getResources().getString(R.string.date_time)).getSeconds() * 1000 ) ;
                                    DateTime dtMalaysia = dt.withZone(DateTimeZone.forID("Asia/Kuala_Lumpur"));
                                    //appointment_date_textview.setText(dtMalaysia.toString("yyyy-MM-dd"));
                                    //appointment_time_textview.setText(dtMalaysia.toString("hh:mm a"));
                                    appointmentTimeDrpDown.setText(arrayAdapter.getItem(arrayAdapter.getPosition(dtMalaysia.toString("hh:mm a"))),false);
                                    specialityDrpDown.setText(adapter.getItem(adapter.getPosition(document.getString(getString(R.string.specialityName)))),false);

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

        }

    }

}