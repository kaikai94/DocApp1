package com.example.docapp.patient.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.docapp.PatientNavigation;
import com.example.docapp.R;
import com.example.docapp.databinding.ActivityPaymentBinding;
import com.example.docapp.patient.ui.appointment.AppointmentFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private String appointmentID;
    private static String TAG = "Payment";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        final Button payBtn = binding.payBtn;

        if(getIntent().hasExtra(getString(R.string.appointment_id)))
        {
            Log.d(TAG, "GOT DATA");
            appointmentID = getIntent().getStringExtra(getString(R.string.appointment_id));
        }

        payBtn.setOnClickListener(v -> {
            Log.d(TAG, "CLICKED");
            DocumentReference documentRef = fStore.collection(getString(R.string.collection_appointments)).document(appointmentID);

            documentRef
                    .update(getString(R.string.status), getString(R.string.status_pending_approve))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(getApplicationContext(), "Successful pay. Please wait for approval.", Toast.LENGTH_SHORT);

                            //Intent intent = new Intent(getApplicationContext(), PatientNavigation.class);
                            //startActivity(intent);
                            finish();

                                    getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.nav_host_fragment_content_patient_navigation, new AppointmentFragment())
                                            .addToBackStack(null)
                                            .commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        });
    }
}