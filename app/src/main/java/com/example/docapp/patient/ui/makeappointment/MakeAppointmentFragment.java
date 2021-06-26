package com.example.docapp.patient.ui.makeappointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.docapp.classes.Doctor;
import com.example.docapp.R;
import com.example.docapp.classes.Utilities;
import com.example.docapp.databinding.FragmentMakeAppointmentBinding;
import com.example.docapp.patient.ui.PaymentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.example.docapp.classes.DoctorSpeciality;

public class MakeAppointmentFragment extends Fragment {
    private final static String TAG = "Make Appoinment";
    private FragmentMakeAppointmentBinding binding;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    List<DoctorSpeciality> specialityList = new ArrayList<>();
    List<Doctor> doctor_List = new ArrayList<>();
    int selectedDocID;

    MaterialAutoCompleteTextView docNameDrpDown, docSpecialityDrpDown;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMakeAppointmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        docSpecialityDrpDown = binding.docSpecialityTextView;
        docNameDrpDown = binding.docNameTextView;
        final Button submitBtn = binding.submitBtn;
        final DatePicker datePicker = binding.datePicker;
        final AutoCompleteTextView timeSlotTextView = binding.timeSlotTextView;

        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(getContext(), R.array.timeslot, android.R.layout.simple_list_item_1);
        timeSlotTextView.setAdapter(aa);

        submitBtn.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year =  datePicker.getYear();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            Date time;
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            try {
                time = sdf.parse(timeSlotTextView.getText().toString());
                calendar.set(year, month, day, time.getHours(), time.getMinutes(), 0);
            } catch (ParseException e) {

            }

            Map<String, Object> appointment = new HashMap<>();
            appointment.put(getResources().getString(R.string.doctorName), docNameDrpDown.getText().toString());
            appointment.put(getResources().getString(R.string.specialityName), docSpecialityDrpDown.getText().toString());
            appointment.put(getResources().getString(R.string.doctorId), doctor_List.get(selectedDocID).getDocumentId());
            appointment.put(getResources().getString(R.string.date_time), new Timestamp(calendar.getTime()));
            appointment.put(getResources().getString(R.string.userId), fAuth.getCurrentUser().getUid());
            appointment.put(getResources().getString(R.string.status), getResources().getString(R.string.status_pending_payment));

            // Add a new document with a generated ID
            fStore.collection(getResources().getString(R.string.collection_appointments))
                    .add(appointment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), "Successful make appointment!", Toast.LENGTH_SHORT).show();

                            PaymentFragment frag = new PaymentFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString(getResources().getString(R.string.appointment_id), documentReference.getId());
                            frag.setArguments(bundle);

                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(MakeAppointmentFragment.this)
                                    .replace(R.id.nav_host_fragment_content_patient_navigation, frag, "Payment")
                                    .addToBackStack(null)
                                    .commit();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        });

        docNameDrpDown.setOnItemClickListener((parent, view, position, id) ->
                selectedDocID = position
        );

        List<String> tempList = new ArrayList<>();

        Utilities utilities = Utilities.getInstance();

        utilities.getSpecialityList(list -> {
            specialityList = list;
            tempList.clear();
            list.forEach(cat -> tempList.add(cat.getName()));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, tempList);
            docSpecialityDrpDown.setAdapter(adapter);
        });

        docSpecialityDrpDown.setOnItemClickListener((parent, view, position, id) -> {
            doctor_List.clear();
            List<String> temp = new ArrayList<>();

            readDoctorData(list -> {
                temp.clear();
                list.forEach(doc -> { temp.add(doc.getName());});
                ArrayAdapter<String> adapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_dropdown_item_1line, temp);
                docNameDrpDown.setAdapter(adapter);
            },String.valueOf(specialityList.get(position).getDocumentId()));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    private interface FireStoreDoctorCallback {
        void onCallback(List<Doctor> list);
    }

    private void readDoctorData(FireStoreDoctorCallback firestoreCallback, String docID){
        fStore
                .collection(getResources().getString(R.string.collection_doctors))
                .whereEqualTo(getResources().getString(R.string.specialityId), docID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {

                        Doctor temp = new Doctor();
                        temp.setDocumentId(doc.getId());
                        temp.setName(doc.getString(getResources().getString(R.string.name)));
                        doctor_List.add(temp);
                    }
                    firestoreCallback.onCallback(doctor_List);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}